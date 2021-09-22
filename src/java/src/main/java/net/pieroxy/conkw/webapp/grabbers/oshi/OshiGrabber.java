package net.pieroxy.conkw.webapp.grabbers.oshi;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.model.ResponseData;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.util.EdidUtil;

import java.util.*;
import java.util.stream.Collectors;

public class OshiGrabber extends AsyncGrabber<SimpleCollector> {
  static final String NAME = "oshi";

  public static final String CONFIG_STATIC_DATA_DELAY="staticDataDelay";
  public static final String CONFIG_DETAILED_DATA_DELAY="detailedDataDelay";

  private CDuration staticDataDelay = CDurationParser.parse("1d");
  private CDuration detailedDataDelay = CDuration.ONE_MINUTE;
  private Map<Integer, OSProcess> previousProcesses;
  OSHIExtractor extractor = new OSHIExtractor();
  long[]ticks;
  long[][]ticksByCpu;
  Map<String, IOStats> disksStats;
  Map<String, IOStats> netStats;

  private long lastGrabSync = -1;

  private class IOStats {
    long reads;
    long writes;
  }

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
  }

  @Override
  public void grabSync(SimpleCollector c) {
    long now = System.currentTimeMillis();
    lastGrabSync = now;

    // Garbage generated and time elapsed are measured on my computer. They give an order of magnitude.
    // Overall: 146m / 1.2s
    ResponseData res = new ResponseData(this, System.currentTimeMillis());
    extract(c, "sensors", this::extractSensors, CDuration.ZERO); // 250k
    extract(c, "memory", this::extractMemory, CDuration.ZERO); // 41k
    extract(c, "physicalmemory", this::extractPhysicalMemory, staticDataDelay); // 8k
    extract(c, "virtualmemory", this::extractVirtualMemory, CDuration.ZERO); // 193k
    extract(c, "computer", this::extractComputer, staticDataDelay); // 9k
    extract(c, "baseboard", this::extractBaseboard, staticDataDelay); // 9k
    extract(c, "firmware", this::extractFirmware, staticDataDelay); // 9k
    extract(c, "cpu", this::extractCpu, CDuration.ZERO); // 1.2m / 70ms
    extract(c, "cpubycore", this::extractCpuByCore, CDuration.ZERO); // 70k
    extract(c, "cpuident", this::extractCpuIdent, staticDataDelay); // 9k
    extract(c, "displays", this::extractDisplays, staticDataDelay); // 26k
    extract(c, "disksio", this::extractDisksIo, CDuration.ZERO); // 11k
    extract(c, "disksinfos", this::extractDisks, detailedDataDelay); // 20k
    extract(c, "graphicscards", this::extractGraphicsCards, staticDataDelay); // 10k
    extract(c, "nics", this::extractNics, detailedDataDelay); // 12k
    extract(c, "netbw", this::extractNetbw, CDuration.ZERO); // 12k
    extract(c, "battery", this::extractBattery, CDuration.FIVE_SECONDS); // 8k
    extract(c, "psus", this::extractPsus, detailedDataDelay); // 8k
    extract(c, "soundcards", this::extractSoundCards, staticDataDelay); // 9k
    extract(c, "usb", this::extractUsbDevices, detailedDataDelay); // 17k
    extract(c, "os", this::extractOperatingSystem, detailedDataDelay); // 350k
    extract(c, "filestores", this::extractFileStores, CDuration.ZERO); // 28k
    extract(c, "tcpv4", this::extractTcpv4, detailedDataDelay); // 9k
    extract(c, "tcpv6", this::extractTcpv6, detailedDataDelay); // 9k
    extract(c, "udpv4", this::extractUdpv4, detailedDataDelay); // 9k
    extract(c, "udpv6", this::extractUdpv6, detailedDataDelay); // 9k
    extract(c, "sessions", this::extractSessions, detailedDataDelay); // 10k
    extract(c, "shortsessions", this::extractShortSessions, CDuration.ZERO); // 9k
    extract(c, "netp", this::extractNetworkParams, detailedDataDelay); // 198k
    extract(c, "processes", this::extractProcesses, detailedDataDelay); // 125m / 400ms
    extract(c, "services", this::extractServices, detailedDataDelay); // 20m / 400ms
  }

  private void extractServices(SimpleCollector c) {
    int i=0;
    for (OSService p : extractor.getServices()) {
      c.collect("services_name_"+i, p.getName());
      c.collect("services_process_id_"+i, p.getProcessID());
      c.collect("services_state_"+i, p.getState().name());
      i++;
    }
    c.collect("services_count", i);
  }

  private void extractProcesses(SimpleCollector c) {
    if (previousProcesses == null) previousProcesses = new HashMap<>();
    int i=0;
    List<OSProcess> procs = extractor.getProcesses();
    Map<Integer, OSProcess> newP = new HashMap<>();
    procs.forEach(p -> newP.put(p.getProcessID(), p));
    Collections.sort(procs, Comparator.comparingDouble(this::getInstantCpu).reversed());
    while (procs.size()>50) procs.remove(procs.size()-1);
    for (OSProcess p : procs) {
      c.collect("processes_affinity_mask_"+i, p.getAffinityMask());
      c.collect("processes_bitness_"+i, p.getBitness());
      c.collect("processes_name_"+i, p.getName());
      c.collect("processes_bytes_read_"+i, p.getBytesRead());
      c.collect("processes_bytes_written_"+i, p.getBytesWritten());
      c.collect("processes_command_line_"+i, p.getCommandLine().replace((char)0,(char)32));
      c.collect("processes_current_working_directory_"+i, p.getCurrentWorkingDirectory());
      c.collect("processes_group_"+i, p.getGroup());
      c.collect("processes_group_id_"+i, p.getGroupID());
      c.collect("processes_kernel_time_"+i, p.getKernelTime());
      c.collect("processes_major_faults_"+i, p.getMajorFaults());
      c.collect("processes_minor_faults_"+i, p.getMinorFaults());
      c.collect("processes_open_files_"+i, p.getOpenFiles());
      c.collect("processes_parent_process_id_"+i, p.getParentProcessID());
      c.collect("processes_path_"+i, p.getPath());
      c.collect("processes_priority_"+i, p.getPriority());
      c.collect("processes_process_cpu_load_instant_"+i, getInstantCpu(p));
      c.collect("processes_process_cpu_load_cumulative_"+i, p.getProcessCpuLoadCumulative());
      c.collect("processes_process_id_"+i, p.getProcessID());
      c.collect("processes_resident_set_size_"+i, p.getResidentSetSize());
      c.collect("processes_start_time_"+i, p.getStartTime());
      c.collect("processes_thread_count_"+i, p.getThreadCount());
      c.collect("processes_up_time_"+i, p.getUpTime());
      c.collect("processes_user_"+i, p.getUser());
      c.collect("processes_user_id_"+i, p.getUserID());
      c.collect("processes_user_time_"+i, p.getUserTime());
      c.collect("processes_virtual_size_"+i, p.getVirtualSize());
      c.collect("processes_state_"+i, p.getState().name());
      i++;
    }
    previousProcesses = newP;
    c.collect("processes_count", i);
  }

  private double getInstantCpu(OSProcess p) {
    OSProcess old = previousProcesses.get(p.getProcessID());
    return old == null ? p.getProcessCpuLoadCumulative() : p.getProcessCpuLoadBetweenTicks(old);
  }

  private void extractNetworkParams(SimpleCollector c) {
    c.collect("netp_domain_name", extractor.getNetworkParams().getDomainName());
    c.collect("netp_host_name", extractor.getNetworkParams().getHostName());
    c.collect("netp_ipv4_default_gateway", extractor.getNetworkParams().getIpv4DefaultGateway());
    c.collect("netp_ipv6_default_gateway", extractor.getNetworkParams().getIpv6DefaultGateway());
    c.collect("netp_dns", Arrays.stream(extractor.getNetworkParams().getDnsServers()).collect(Collectors.joining(" ")));
  }

  private void extractShortSessions(SimpleCollector c) {
    Map<String, Integer> sessionsCount = new HashMap<>();
    for (OSSession s : extractor.getSessions()) {
      String user = s.getUserName();
      Integer i = sessionsCount.get(user);
      if (i==null) i=0;
      i++;
      sessionsCount.put(user, i);
    }
    int i=0;
    for (String user : sessionsCount.keySet()) {
      c.collect("shortsessions_name_"+i, user);
      c.collect("shortsessions_nbs_"+i, sessionsCount.get(user));
      i++;
    }
    c.collect("shortsessions_count", i);
  }

  private void extractSessions(SimpleCollector c) {
    int i=0;
    for (OSSession s : extractor.getSessions()) {
      c.collect("sessions_host_"+i, s.getHost());
      c.collect("sessions_login_time_"+i, s.getLoginTime());
      c.collect("sessions_user_name_"+i, s.getUserName());
      c.collect("sessions_terminal_device_"+i, s.getTerminalDevice());
      i++;
    }
    c.collect("sessions_count", i);
  }

  private void extractUdpv6(SimpleCollector c) {
    c.collect("udpv6_datagrams_no_port", extractor.getNetStatsUdpv6().getDatagramsNoPort());
    c.collect("udpv6_datagrams_received", extractor.getNetStatsUdpv6().getDatagramsReceived());
    c.collect("udpv6_datagrams_sent", extractor.getNetStatsUdpv6().getDatagramsSent());
    c.collect("udpv6_datagrams_received_errors", extractor.getNetStatsUdpv6().getDatagramsReceivedErrors());
  }

  private void extractUdpv4(SimpleCollector c) {
    c.collect("udpv4_datagrams_no_port", extractor.getNetStatsUdpv4().getDatagramsNoPort());
    c.collect("udpv4_datagrams_received", extractor.getNetStatsUdpv4().getDatagramsReceived());
    c.collect("udpv4_datagrams_sent", extractor.getNetStatsUdpv4().getDatagramsSent());
    c.collect("udpv4_datagrams_received_errors", extractor.getNetStatsUdpv4().getDatagramsReceivedErrors());
  }

  private void extractTcpv6(SimpleCollector c) {
    c.collect("tcpv6_connection_failures", extractor.getNetStatsIpv6().getConnectionFailures());
    c.collect("tcpv6_connections_active", extractor.getNetStatsIpv6().getConnectionsActive());
    c.collect("tcpv6_connections_established", extractor.getNetStatsIpv6().getConnectionsEstablished());
    c.collect("tcpv6_out_resets", extractor.getNetStatsIpv6().getOutResets());
    c.collect("tcpv6_connections_passive", extractor.getNetStatsIpv6().getConnectionsPassive());
    c.collect("tcpv6_connections_reset", extractor.getNetStatsIpv6().getConnectionsReset());
    c.collect("tcpv6_in_errors", extractor.getNetStatsIpv6().getInErrors());
    c.collect("tcpv6_segments_received", extractor.getNetStatsIpv6().getSegmentsReceived());
    c.collect("tcpv6_segments_retransmitted", extractor.getNetStatsIpv6().getSegmentsRetransmitted());
    c.collect("tcpv6_segments_sent", extractor.getNetStatsIpv6().getSegmentsSent());
  }

  private void extractTcpv4(SimpleCollector c) {
    c.collect("tcpv4_connection_failures", extractor.getNetStatsIpv4().getConnectionFailures());
    c.collect("tcpv4_connections_active", extractor.getNetStatsIpv4().getConnectionsActive());
    c.collect("tcpv4_connections_established", extractor.getNetStatsIpv4().getConnectionsEstablished());
    c.collect("tcpv4_out_resets", extractor.getNetStatsIpv4().getOutResets());
    c.collect("tcpv4_connections_passive", extractor.getNetStatsIpv4().getConnectionsPassive());
    c.collect("tcpv4_connections_reset", extractor.getNetStatsIpv4().getConnectionsReset());
    c.collect("tcpv4_in_errors", extractor.getNetStatsIpv4().getInErrors());
    c.collect("tcpv4_segments_received", extractor.getNetStatsIpv4().getSegmentsReceived());
    c.collect("tcpv4_segments_retransmitted", extractor.getNetStatsIpv4().getSegmentsRetransmitted());
    c.collect("tcpv4_segments_sent", extractor.getNetStatsIpv4().getSegmentsSent());
  }

  private void extractFileStores(SimpleCollector c) {
    int i=0;
    for (OSFileStore fs : extractor.getFileStores()) {
      c.collect("filestores_name_"+i, fs.getName());
      c.collect("filestores_description_"+i, fs.getDescription());
      c.collect("filestores_type_"+i, fs.getType());
      c.collect("filestores_label_"+i, fs.getLabel());
      c.collect("filestores_mount_"+i, fs.getMount());
      c.collect("filestores_logical_volume_"+i, fs.getLogicalVolume());
      c.collect("filestores_options_"+i, fs.getOptions());
      c.collect("filestores_uuid_"+i, fs.getUUID());
      c.collect("filestores_volume_"+i, fs.getVolume());
      c.collect("filestores_free_space_"+i, fs.getFreeSpace());
      c.collect("filestores_total_space_"+i, fs.getTotalSpace());
      c.collect("filestores_usable_space_"+i, fs.getUsableSpace());
      i++;
    }
    c.collect("filestores_count", i);
  }

  private void extractOperatingSystem(SimpleCollector c) {
    c.collect("os_manufacturer", extractor.getOperatingSystem().getManufacturer());
    c.collect("os_family", extractor.getOperatingSystem().getFamily());
    c.collect("os_bitness", extractor.getOperatingSystem().getBitness());
    c.collect("os_process_count", extractor.getOperatingSystem().getProcessCount());
    c.collect("os_process_id", extractor.getOperatingSystem().getProcessId());
    c.collect("os_system_boot_time", extractor.getOperatingSystem().getSystemBootTime());
    c.collect("os_system_uptime", extractor.getOperatingSystem().getSystemUptime());
    c.collect("os_thread_count", extractor.getOperatingSystem().getThreadCount());
    c.collect("os_version", extractor.getVersionInfo().getVersion());
    c.collect("os_build_number", extractor.getVersionInfo().getBuildNumber());
    c.collect("os_code_name", extractor.getVersionInfo().getCodeName());
    c.collect("os_max_file_descriptors", extractor.getFileSystem().getMaxFileDescriptors());
    c.collect("os_open_file_descriptors", extractor.getFileSystem().getOpenFileDescriptors());
  }

  private void extractUsbDevices(SimpleCollector c) {
    int i=0;
    for (UsbDevice u : extractor.getUsbDevices()) {
      c.collect("usb_name_"+i, u.getName());
      c.collect("usb_serial_number_"+i, u.getSerialNumber());
      c.collect("usb_vendor_"+i, u.getVendor());
      c.collect("usb_vendor_id_"+i, u.getVendorId());
      c.collect("usb_product_id_"+i, u.getProductId());
      c.collect("usb_unique_device_id_"+i, u.getUniqueDeviceId());
      i++;
    }
    c.collect("usb_count", i);
  }

  private void extractSoundCards(SimpleCollector c) {
    int i=0;
    for (SoundCard sc : extractor.getSoundCards()) {
      c.collect("soundcards_name_"+i, sc.getName());
      c.collect("soundcards_codec_"+i, sc.getCodec());
      c.collect("soundcards_driver_version_"+i, sc.getDriverVersion());
      i++;
    }
    c.collect("soundcards_count", i);
  }

  private void extractBattery(SimpleCollector c) {
    for (PowerSource psu : extractor.getPowerSources()) {
      double d = psu.getRemainingCapacityPercent();
      if (d >= 0) c.collect("battery_remaining_prc", d);
      c.collect("battery_plugged", (psu.isPowerOnLine() || psu.isCharging() || !psu.isDischarging())?1:0);
    }
  }

  private void extractPsus(SimpleCollector c) {
    int i=0;
    for (PowerSource psu : extractor.getPowerSources()) {
      c.collect("psus_name_"+i, psu.getName());
      c.collect("psus_manufacturer_"+i, psu.getManufacturer());
      c.collect("psus_serial_number_"+i, psu.getSerialNumber());
      c.collect("psus_chemistry_"+i, psu.getChemistry());
      c.collect("psus_device_name_"+i, psu.getDeviceName());
      c.collect("psus_amperage_"+i, psu.getAmperage());
      c.collect("psus_capacity_units_"+i, psu.getCapacityUnits().name());
      c.collect("psus_current_capacity_"+i, psu.getCurrentCapacity());
      c.collect("psus_cycle_count_"+i, psu.getCycleCount());
      c.collect("psus_design_capacity_"+i, psu.getDesignCapacity());
      if (psu.getManufactureDate()!=null) c.collect("psus_manufacture_date_"+i, psu.getManufactureDate().getYear());
      c.collect("psus_power_usage_rate_"+i, psu.getPowerUsageRate());
      c.collect("psus_remaining_capacity_percent_"+i, psu.getRemainingCapacityPercent());
      c.collect("psus_temperature_"+i, psu.getTemperature());
      c.collect("psus_time_remaining_estimated_"+i, psu.getTimeRemainingEstimated());
      c.collect("psus_voltage_"+i, psu.getVoltage());
      c.collect("psus_time_remaining_instant_"+i, psu.getTimeRemainingInstant());
      c.collect("psus_in_charge_"+i, psu.isCharging()?1:0);
      c.collect("psus_in_discharge_"+i, psu.isDischarging()?1:0);
      c.collect("psus_power_online_"+i, psu.isPowerOnLine()?1:0);
      i++;
    }
    c.collect("psus_count", i);
  }

  private void extractNics(SimpleCollector c) {
    int i=0;
    for (NetworkIF nic : extractor.getNICs()) {
      c.collect("nics_name_"+i, nic.getName());
      c.collect("nics_display_name_"+i, nic.getDisplayName());
      c.collect("nics_macaddr_"+i, nic.getMacaddr());
      c.collect("nics_bytes_recv_"+i, nic.getBytesRecv());
      c.collect("nics_bytes_sent_"+i, nic.getBytesSent());
      c.collect("nics_if_type_"+i, nic.getIfType());
      c.collect("nics_speed_"+i, nic.getSpeed()/8);
      c.collect("nics_ipv4_"+i, Arrays.stream(nic.getIPv4addr()).collect(Collectors.joining(" ")));
      c.collect("nics_ipv6_"+i, Arrays.stream(nic.getIPv6addr()).collect(Collectors.joining(" ")));
      c.collect("nics_subnet_mask_"+i, Arrays.stream(nic.getSubnetMasks()).map(l -> ""+l).collect(Collectors.joining(" ")));
      i++;
    }
    c.collect("nics_count", i);
  }

  private void extractNetbw(SimpleCollector c) {
    if (netStats==null) netStats = new HashMap<>();
    int i=0;
    long totalin=0, totalout=0, totalspeed=0;
    boolean globalextract = true;
    for (NetworkIF nic : extractor.getNICs()) {
      String name = nic.getName();
      IOStats stats = netStats.get(name);
      boolean extract = true;
      if (stats == null) {
        netStats.put(name, stats = new IOStats());
        extract = globalextract = false;
      }
      long in = nic.getBytesRecv() - stats.reads;
      long out = nic.getBytesSent() - stats.writes;
      if (extract) {
        computeAutoMaxPerSecond(c, "netbw_in_"+i, in);
        computeAutoMaxPerSecond(c, "netbw_out_"+i, out);
        c.collect("netbw_speed_"+i, nic.getSpeed()/8);
      }
      totalin+=in;
      totalout+=out;
      totalspeed+=nic.getSpeed()/8;
      stats.writes = nic.getBytesSent();
      stats.reads = nic.getBytesRecv();
      i++;
    }
    c.collect("netbw_count", i);
    if (globalextract) {
      computeAutoMaxPerSecond(c, "netbw_in", totalin);
      computeAutoMaxPerSecond(c, "netbw_out", totalout);
      c.collect("netbw_speed", totalspeed);
    }
  }

  private void extractGraphicsCards(SimpleCollector c) {
    int i=0;
    for (GraphicsCard gc : extractor.getGraphicsCards()) {
      c.collect("graphicscards_name_"+i, gc.getName());
      c.collect("graphicscards_vendor_"+i, gc.getVendor());
      c.collect("graphicscards_device_id_"+i, gc.getDeviceId());
      c.collect("graphicscards_version_"+i, gc.getVersionInfo());
      i++;
    }
    c.collect("graphicscards_count", i);
  }

  private void extractDisksIo(SimpleCollector c) {
    if (disksStats == null) disksStats =  new HashMap<>();
    double tr=0,tw=0;
    StringBuilder allnames = new StringBuilder();
    for (HWDiskStore d : extractor.getDisks()) {
      String name = d.getName();

      IOStats ds = disksStats.get(name);
      if (ds == null) {
        disksStats.put(d.getName(), ds = new IOStats());
      } else {
        long r=d.getReadBytes()-ds.reads;
        long w=d.getWriteBytes()-ds.writes;
        computeAutoMaxPerSecond(c, "diskios_read_bytes_"+name, r);
        computeAutoMaxPerSecond(c, "diskios_write_bytes_"+name, w);
        tr+=r;
        tw+=w;
        if (allnames.length()>0) allnames.append(',');
        allnames.append(name);
      }
      ds.reads = d.getReadBytes();
      ds.writes = d.getWriteBytes();
    }
    computeAutoMaxPerSecond(c, "diskios_read_bytes", tr);
    computeAutoMaxPerSecond(c, "diskios_write_bytes", tw);
    c.collect("diskios_disks", allnames.toString());
  }

  private void extractDisks(SimpleCollector c) {
    for (HWDiskStore d : extractor.getDisks()) {
      String name = d.getName();
      c.collect("diskinfos_model_"+name, d.getModel());
      c.collect("diskinfos_serial_"+name, d.getSerial());
      c.collect("diskinfos_current_queue_length_"+name, d.getCurrentQueueLength());
      c.collect("diskinfos_read_bytes_"+name, d.getReadBytes());
      c.collect("diskinfos_reads_"+name, d.getReads());
      c.collect("diskinfos_size_"+name, d.getSize());
      c.collect("diskinfos_timestamp_"+name, d.getTimeStamp());
      c.collect("diskinfos_transfer_time_"+name, d.getTransferTime());
      c.collect("diskinfos_write_bytes_"+name, d.getWriteBytes());
      c.collect("diskinfos_writes_"+name, d.getWrites());
    }
    c.collect("diskinfos_names", extractor.getDisks().stream().map(di -> di.getName()).collect(Collectors.joining(",")));
  }

  private void extractDisplays(SimpleCollector c) {
    int i=0;
    for (Display d : extractor.getDisplays()) {
      c.collect("displays_digital_" + i, EdidUtil.isDigital(d.getEdid())?1:0);
      c.collect("displays_manufacturer_" + i, EdidUtil.getManufacturerID(d.getEdid()));
      c.collect("displays_product_id_"+i, EdidUtil.getProductID(d.getEdid()));
      c.collect("displays_serial_"+i, EdidUtil.getSerialNo(d.getEdid()));
      c.collect("displays_version_"+i, EdidUtil.getVersion(d.getEdid()));
      c.collect("displays_week_"+i, EdidUtil.getWeek(d.getEdid()));
      c.collect("displays_year_"+i, EdidUtil.getYear(d.getEdid()));
      i++;
    }
    c.collect("displays_count", i);
  }

  private void extractCpuIdent(SimpleCollector c) {
    c.collect("cpuident_identifier", extractor.getProcessorIdentifier().getIdentifier());
    c.collect("cpuident_name", extractor.getProcessorIdentifier().getName());
    c.collect("cpuident_model", extractor.getProcessorIdentifier().getModel());
    c.collect("cpuident_processor_id", extractor.getProcessorIdentifier().getProcessorID());
    c.collect("cpuident_family", extractor.getProcessorIdentifier().getFamily());
    c.collect("cpuident_micro_arch", extractor.getProcessorIdentifier().getMicroarchitecture());
    c.collect("cpuident_vendor", extractor.getProcessorIdentifier().getVendor());
    c.collect("cpuident_vendor_freq", extractor.getProcessorIdentifier().getVendorFreq());
    c.collect("cpuident_stepping", extractor.getProcessorIdentifier().getStepping());
    c.collect("cpuident_64bits", extractor.getProcessorIdentifier().isCpu64bit()?1:0);

  }

  private void extractCpu(SimpleCollector c) {
    c.collect("cpu_context_switches", extractor.getProcessor().getContextSwitches());
    c.collect("cpu_interrupts", extractor.getProcessor().getInterrupts());
    c.collect("cpu_logical_processor_count", extractor.getProcessor().getLogicalProcessorCount());
    c.collect("cpu_physical_processor_count", extractor.getProcessor().getPhysicalProcessorCount());
    c.collect("cpu_max_freq", extractor.getProcessor().getMaxFreq());
    c.collect("cpu_physical_package_count", extractor.getProcessor().getPhysicalPackageCount());
    double[]la = extractor.getProcessor().getSystemLoadAverage(3);
    c.collect("cpu_load_1", la[0]);
    c.collect("cpu_load_2", la[1]);
    c.collect("cpu_load_3", la[2]);
    if (ticks != null) {
      c.collect("cpu_load", extractor.getProcessor().getSystemCpuLoadBetweenTicks(ticks));
    }
    ticks = extractor.getProcessor().getSystemCpuLoadTicks();
  }

  private void extractCpuByCore(SimpleCollector c) {
    if (ticksByCpu != null) {
      double[]bycore = extractor.getProcessor().getProcessorCpuLoadBetweenTicks(ticksByCpu);
      for (int i=0 ; i<bycore.length ; i++) {
        c.collect("cpubycore_load_" + i, bycore[i]);
      }
      c.collect("cpubycore_count", bycore.length);

    }
    ticksByCpu = extractor.getProcessor().getProcessorCpuLoadTicks();
    // Nothing interesting there
    // extractor.getProcessor().getLogicalProcessors().forEach(lp -> lp.?);
  }

  private void extractFirmware(SimpleCollector c) {
    c.collect("firmware_manufacturer", extractor.getFirmware().getManufacturer());
    c.collect("firmware_name", extractor.getFirmware().getName());
    c.collect("firmware_description", extractor.getFirmware().getDescription());
    c.collect("firmware_version", extractor.getFirmware().getVersion());
    c.collect("firmware_release_date", extractor.getFirmware().getReleaseDate());
  }

  private void extractBaseboard(SimpleCollector c) {
    c.collect("baseboard_manufacturer", extractor.getBaseboard().getManufacturer());
    c.collect("baseboard_model", extractor.getBaseboard().getModel());
    c.collect("baseboard_serial_number", extractor.getBaseboard().getSerialNumber());
    c.collect("baseboard_version", extractor.getBaseboard().getVersion());
  }

  private void extractVirtualMemory(SimpleCollector c) {
    c.collect("virtualmemory_max", extractor.getVirtualMemory().getVirtualMax());
    c.collect("virtualmemory_in_use", extractor.getVirtualMemory().getVirtualInUse());
    c.collect("virtualmemory_swap_in", extractor.getVirtualMemory().getSwapPagesIn());
    c.collect("virtualmemory_swap_out", extractor.getVirtualMemory().getSwapPagesOut());
    c.collect("virtualmemory_swap_total", extractor.getVirtualMemory().getSwapTotal());
    c.collect("virtualmemory_swap_used", extractor.getVirtualMemory().getSwapUsed());
  }

  private void extractPhysicalMemory(SimpleCollector c) {
    int i=0;
    for (PhysicalMemory ps : extractor.getPhysicalMemory()) {
      c.collect("physicalmemory_name_"+i, ps.getBankLabel());
      c.collect("physicalmemory_capacity_"+i, ps.getCapacity());
      c.collect("physicalmemory_type_"+i, ps.getMemoryType());
      c.collect("physicalmemory_clock_"+i, ps.getClockSpeed());
      c.collect("physicalmemory_manufacturer_"+i, ps.getManufacturer());
      i++;
    }
    c.collect("physicalmemory_nb", i);
  }

  private void extractComputer(SimpleCollector c) {
    c.collect("computer_manufacturer", extractor.getComputerSystem().getManufacturer());
    c.collect("computer_UUID", extractor.getComputerSystem().getHardwareUUID());
    c.collect("computer_model", extractor.getComputerSystem().getModel());
    c.collect("computer_serial_number", extractor.getComputerSystem().getSerialNumber());
  }

  private void extractMemory(SimpleCollector c) {
    long av = extractor.getMemory().getAvailable();
    long total = extractor.getMemory().getTotal();
    c.collect("memory_available", av);
    c.collect("memory_total", total);
    c.collect("memory_in_use", total-av);
    c.collect("memory_page_size", extractor.getMemory().getPageSize());
  }

  private void extractSensors(SimpleCollector c) {
    int i=0;
    for (int speed : extractor.getSensors().getFanSpeeds()) {
      c.collect("sensors_fan_speed_"+i++, speed);
    }
    c.collect("sensors_fan_count", i);
    c.collect("sensors_cputemp", extractor.getSensors().getCpuTemperature());
    c.collect("sensors_cpuvolt", extractor.getSensors().getCpuVoltage());
  }

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    String ddd = config.get(CONFIG_DETAILED_DATA_DELAY);
    if (!StringUtil.isNullOrEmptyTrimmed(ddd)) {
      detailedDataDelay = CDurationParser.parse(ddd);
    }
    String sdd = config.get(CONFIG_STATIC_DATA_DELAY);
    if (sdd!=null) {
      staticDataDelay = CDurationParser.parse(sdd);
    }
  }
}
