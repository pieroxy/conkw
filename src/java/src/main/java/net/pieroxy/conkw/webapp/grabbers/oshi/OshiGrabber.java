package net.pieroxy.conkw.webapp.grabbers.oshi;

import net.pieroxy.conkw.webapp.grabbers.AsyncGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.util.EdidUtil;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OshiGrabber extends AsyncGrabber {
  static final String NAME = "oshi";

  OSHIExtractor extractor = new OSHIExtractor();
  long[]ticks;
  long[][]ticksByCpu;
  Map<String, DiskStats> disksStats;

  private class DiskStats {
    long reads;
    long writes;
  }

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public ResponseData grabSync() {
    // Garbage generated and time elapsed are measured on my computer. They give an order of magnitude.
    // Overall: 146m / 1.2s
    ResponseData res = new ResponseData(getName(), System.currentTimeMillis());
    if (shouldExtract("sensors")) extractSensors(res); // 250k
    if (shouldExtract("memory")) extractMemory(res); // 41k
    if (shouldExtract("physicalmemory")) extractPhysicalMemory(res); // 8k
    if (shouldExtract("virtualmemory")) extractVirtualMemory(res); // 193k
    if (shouldExtract("computer")) extractComputer(res); // 9k
    if (shouldExtract("baseboard")) extractBaseboard(res); // 9k
    if (shouldExtract("firmware")) extractFirmware(res); // 9k
    if (shouldExtract("cpu")) extractCpu(res); // 1.2m / 70ms
    if (shouldExtract("cpubycore")) extractCpuByCore(res); // 70k
    if (shouldExtract("cpuident")) extractCpuIdent(res); // 9k
    if (shouldExtract("displays")) extractDisplays(res); // 26k
    if (shouldExtract("disksio")) extractDisksIo(res); // 11k
    if (shouldExtract("disksinfos")) extractDisks(res); // 20k
    if (shouldExtract("graphicscards")) extractGraphicsCards(res); // 10k
    if (shouldExtract("nics")) extractNics(res); // 12k
    if (shouldExtract("battery")) extractBattery(res); // 8k
    if (shouldExtract("psus")) extractPsus(res); // 8k
    if (shouldExtract("soundcards")) extractSoundCards(res); // 9k
    if (shouldExtract("usb")) extractUsbDevices(res); // 17k
    if (shouldExtract("os")) extractOperatingSystem(res); // 350k
    if (shouldExtract("filestores")) extractFileStores(res); // 28k
    if (shouldExtract("tcpv4")) extractTcpv4(res); // 9k
    if (shouldExtract("tcpv6")) extractTcpv6(res); // 9k
    if (shouldExtract("udpv4")) extractUdpv4(res); // 9k
    if (shouldExtract("udpv6")) extractUdpv6(res); // 9k
    if (shouldExtract("sessions")) extractSessions(res); // 10k
    if (shouldExtract("shortsessions")) extractShortSessions(res); // 9k
    if (shouldExtract("netp")) extractNetworkParams(res); // 198k
    if (shouldExtract("processes")) extractProcesses(res); // 125m / 400ms
    if (shouldExtract("services")) extractServices(res); // 20m / 400ms
    return res;
  }

  private void extractServices(ResponseData res) {
    int i=0;
    for (OSService p : extractor.getServices()) {
      res.addMetric("services_name_"+i, p.getName());
      res.addMetric("services_process_id_"+i, p.getProcessID());
      res.addMetric("services_state_"+i, p.getState().name());
      i++;
    }
    res.addMetric("services_count", i);
  }

  private void extractProcesses(ResponseData res) {
    int i=0;
    for (OSProcess p : extractor.getProcesses()) {
      res.addMetric("processes_affinity_mask_"+i, p.getAffinityMask());
      res.addMetric("processes_bitness_"+i, p.getBitness());
      res.addMetric("processes_name_"+i, p.getName());
      res.addMetric("processes_bytes_read_"+i, p.getBytesRead());
      res.addMetric("processes_bytes_written_"+i, p.getBytesWritten());
      res.addMetric("processes_command_line_"+i, p.getCommandLine().replace((char)0,(char)32));
      res.addMetric("processes_current_working_directory_"+i, p.getCurrentWorkingDirectory());
      res.addMetric("processes_group_"+i, p.getGroup());
      res.addMetric("processes_group_id_"+i, p.getGroupID());
      res.addMetric("processes_kernel_time_"+i, p.getKernelTime());
      res.addMetric("processes_major_faults_"+i, p.getMajorFaults());
      res.addMetric("processes_minor_faults_"+i, p.getMinorFaults());
      res.addMetric("processes_open_files_"+i, p.getOpenFiles());
      res.addMetric("processes_parent_process_id_"+i, p.getParentProcessID());
      res.addMetric("processes_path_"+i, p.getPath());
      res.addMetric("processes_priority_"+i, p.getPriority());
      res.addMetric("processes_process_cpu_load_cumulative_"+i, p.getProcessCpuLoadCumulative());
      res.addMetric("processes_process_id_"+i, p.getProcessID());
      res.addMetric("processes_resident_set_size_"+i, p.getResidentSetSize());
      res.addMetric("processes_start_time_"+i, p.getStartTime());
      res.addMetric("processes_thread_count_"+i, p.getThreadCount());
      res.addMetric("processes_up_time_"+i, p.getUpTime());
      res.addMetric("processes_user_"+i, p.getUser());
      res.addMetric("processes_user_id_"+i, p.getUserID());
      res.addMetric("processes_user_time_"+i, p.getUserTime());
      res.addMetric("processes_virtual_size_"+i, p.getVirtualSize());
      res.addMetric("processes_state_"+i, p.getState().name());
      i++;
    }
    res.addMetric("processes_count", i);
  }

  private void extractNetworkParams(ResponseData res) {
    res.addMetric("netp_domain_name", extractor.getNetworkParams().getDomainName());
    res.addMetric("netp_host_name", extractor.getNetworkParams().getHostName());
    res.addMetric("netp_ipv4_default_gateway", extractor.getNetworkParams().getIpv4DefaultGateway());
    res.addMetric("netp_ipv6_default_gateway", extractor.getNetworkParams().getIpv6DefaultGateway());
    res.addMetric("netp_dns", Arrays.stream(extractor.getNetworkParams().getDnsServers()).collect(Collectors.joining(" ")));
  }

  private void extractShortSessions(ResponseData res) {
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
      res.addMetric("shortsessions_name_"+i, user);
      res.addMetric("shortsessions_nbs_"+i, sessionsCount.get(user));
      i++;
    }
    res.addMetric("shortsessions_count", i);
  }

  private void extractSessions(ResponseData res) {
    int i=0;
    for (OSSession s : extractor.getSessions()) {
      res.addMetric("sessions_host_"+i, s.getHost());
      res.addMetric("sessions_login_time_"+i, s.getLoginTime());
      res.addMetric("sessions_user_name_"+i, s.getUserName());
      res.addMetric("sessions_terminal_device_"+i, s.getTerminalDevice());

      i++;
    }
    res.addMetric("sessions_count", i);
  }

  private void extractUdpv6(ResponseData res) {
    res.addMetric("udpv6_datagrams_no_port", extractor.getNetStatsUdpv6().getDatagramsNoPort());
    res.addMetric("udpv6_datagrams_received", extractor.getNetStatsUdpv6().getDatagramsReceived());
    res.addMetric("udpv6_datagrams_sent", extractor.getNetStatsUdpv6().getDatagramsSent());
    res.addMetric("udpv6_datagrams_received_errors", extractor.getNetStatsUdpv6().getDatagramsReceivedErrors());
  }

  private void extractUdpv4(ResponseData res) {
    res.addMetric("udpv4_datagrams_no_port", extractor.getNetStatsUdpv4().getDatagramsNoPort());
    res.addMetric("udpv4_datagrams_received", extractor.getNetStatsUdpv4().getDatagramsReceived());
    res.addMetric("udpv4_datagrams_sent", extractor.getNetStatsUdpv4().getDatagramsSent());
    res.addMetric("udpv4_datagrams_received_errors", extractor.getNetStatsUdpv4().getDatagramsReceivedErrors());
  }

  private void extractTcpv6(ResponseData res) {
    res.addMetric("tcpv6_connection_failures", extractor.getNetStatsIpv6().getConnectionFailures());
    res.addMetric("tcpv6_connections_active", extractor.getNetStatsIpv6().getConnectionsActive());
    res.addMetric("tcpv6_connections_established", extractor.getNetStatsIpv6().getConnectionsEstablished());
    res.addMetric("tcpv6_out_resets", extractor.getNetStatsIpv6().getOutResets());
    res.addMetric("tcpv6_connections_passive", extractor.getNetStatsIpv6().getConnectionsPassive());
    res.addMetric("tcpv6_connections_reset", extractor.getNetStatsIpv6().getConnectionsReset());
    res.addMetric("tcpv6_in_errors", extractor.getNetStatsIpv6().getInErrors());
    res.addMetric("tcpv6_segments_received", extractor.getNetStatsIpv6().getSegmentsReceived());
    res.addMetric("tcpv6_segments_retransmitted", extractor.getNetStatsIpv6().getSegmentsRetransmitted());
    res.addMetric("tcpv6_segments_sent", extractor.getNetStatsIpv6().getSegmentsSent());
  }

  private void extractTcpv4(ResponseData res) {
    res.addMetric("tcpv4_connection_failures", extractor.getNetStatsIpv4().getConnectionFailures());
    res.addMetric("tcpv4_connections_active", extractor.getNetStatsIpv4().getConnectionsActive());
    res.addMetric("tcpv4_connections_established", extractor.getNetStatsIpv4().getConnectionsEstablished());
    res.addMetric("tcpv4_out_resets", extractor.getNetStatsIpv4().getOutResets());
    res.addMetric("tcpv4_connections_passive", extractor.getNetStatsIpv4().getConnectionsPassive());
    res.addMetric("tcpv4_connections_reset", extractor.getNetStatsIpv4().getConnectionsReset());
    res.addMetric("tcpv4_in_errors", extractor.getNetStatsIpv4().getInErrors());
    res.addMetric("tcpv4_segments_received", extractor.getNetStatsIpv4().getSegmentsReceived());
    res.addMetric("tcpv4_segments_retransmitted", extractor.getNetStatsIpv4().getSegmentsRetransmitted());
    res.addMetric("tcpv4_segments_sent", extractor.getNetStatsIpv4().getSegmentsSent());
  }

  private void extractFileStores(ResponseData res) {
    int i=0;
    for (OSFileStore fs : extractor.getFileStores()) {
      res.addMetric("filestores_name_"+i, fs.getName());
      res.addMetric("filestores_description_"+i, fs.getDescription());
      res.addMetric("filestores_type_"+i, fs.getType());
      res.addMetric("filestores_label_"+i, fs.getLabel());
      res.addMetric("filestores_mount_"+i, fs.getMount());
      res.addMetric("filestores_logical_volume_"+i, fs.getLogicalVolume());
      res.addMetric("filestores_options_"+i, fs.getOptions());
      res.addMetric("filestores_uuid_"+i, fs.getUUID());
      res.addMetric("filestores_volume_"+i, fs.getVolume());
      res.addMetric("filestores_free_space_"+i, fs.getFreeSpace());
      res.addMetric("filestores_total_space_"+i, fs.getTotalSpace());
      res.addMetric("filestores_usable_space_"+i, fs.getUsableSpace());

      i++;
    }
    res.addMetric("usb_count", i);
  }

  private void extractOperatingSystem(ResponseData res) {
    res.addMetric("os_manufacturer", extractor.getOperatingSystem().getManufacturer());
    res.addMetric("os_family", extractor.getOperatingSystem().getFamily());
    res.addMetric("os_bitness", extractor.getOperatingSystem().getBitness());
    res.addMetric("os_process_count", extractor.getOperatingSystem().getProcessCount());
    res.addMetric("os_process_id", extractor.getOperatingSystem().getProcessId());
    res.addMetric("os_system_boot_time", extractor.getOperatingSystem().getSystemBootTime());
    res.addMetric("os_system_uptime", extractor.getOperatingSystem().getSystemUptime());
    res.addMetric("os_thread_count", extractor.getOperatingSystem().getThreadCount());
    res.addMetric("os_version", extractor.getVersionInfo().getVersion());
    res.addMetric("os_build_number", extractor.getVersionInfo().getBuildNumber());
    res.addMetric("os_code_name", extractor.getVersionInfo().getCodeName());
    res.addMetric("os_max_file_descriptors", extractor.getFileSystem().getMaxFileDescriptors());
    res.addMetric("os_open_file_descriptors", extractor.getFileSystem().getOpenFileDescriptors());
  }

  private void extractUsbDevices(ResponseData res) {
    int i=0;
    for (UsbDevice u : extractor.getUsbDevices()) {
      res.addMetric("usb_name"+i, u.getName());
      res.addMetric("usb_serial_number"+i, u.getSerialNumber());
      res.addMetric("usb_vendor"+i, u.getVendor());
      res.addMetric("usb_vendor_id"+i, u.getVendorId());
      res.addMetric("usb_product_id"+i, u.getProductId());
      res.addMetric("usb_unique_device_id"+i, u.getUniqueDeviceId());
      i++;
    }
    res.addMetric("usb_count", i);
  }

  private void extractSoundCards(ResponseData res) {
    int i=0;
    for (SoundCard sc : extractor.getSoundCards()) {
      res.addMetric("soundcards_name"+i, sc.getName());
      res.addMetric("soundcards_codec"+i, sc.getCodec());
      res.addMetric("soundcards_driver_version"+i, sc.getDriverVersion());
    }
    res.addMetric("soundcards_count", i);
  }

  private void extractBattery(ResponseData res) {
    for (PowerSource psu : extractor.getPowerSources()) {
      res.addMetric("battery_remaining_prc", psu.getRemainingCapacityPercent());
    }
  }

  private void extractPsus(ResponseData res) {
    int i=0;
    for (PowerSource psu : extractor.getPowerSources()) {
      res.addMetric("psus_name_"+i, psu.getName());
      res.addMetric("psus_manufacturer_"+i, psu.getManufacturer());
      res.addMetric("psus_serial_number_"+i, psu.getSerialNumber());
      res.addMetric("psus_chemistry_"+i, psu.getChemistry());
      res.addMetric("psus_device_name_"+i, psu.getDeviceName());
      res.addMetric("psus_amperage_"+i, psu.getAmperage());
      res.addMetric("psus_capacity_units_"+i, psu.getCapacityUnits().name());
      res.addMetric("psus_current_capacity_"+i, psu.getCurrentCapacity());
      res.addMetric("psus_cycle_count_"+i, psu.getCycleCount());
      res.addMetric("psus_design_capacity_"+i, psu.getDesignCapacity());
      if (psu.getManufactureDate()!=null) res.addMetric("psus_manufacture_date_"+i, psu.getManufactureDate().getYear());
      res.addMetric("psus_power_usage_rate_"+i, psu.getPowerUsageRate());
      res.addMetric("psus_remaining_capacity_percent_"+i, psu.getRemainingCapacityPercent());
      res.addMetric("psus_temperature_"+i, psu.getTemperature());
      res.addMetric("psus_time_remaining_estimated_"+i, psu.getTimeRemainingEstimated());
      res.addMetric("psus_voltage_"+i, psu.getVoltage());
      res.addMetric("psus_time_remaining_instant_"+i, psu.getTimeRemainingInstant());
    }
    res.addMetric("psus_count", i);
  }

  private void extractNics(ResponseData res) {
    int i=0;
    for (NetworkIF nic : extractor.getNICs()) {
      res.addMetric("nics_name_"+i, nic.getName());
      res.addMetric("nics_display_name_"+i, nic.getDisplayName());
      res.addMetric("nics_macaddr_"+i, nic.getMacaddr());
      res.addMetric("nics_bytes_recv_"+i, nic.getBytesRecv());
      res.addMetric("nics_bytes_sent_"+i, nic.getBytesSent());
      res.addMetric("nics_if_type_"+i, nic.getIfType());
      res.addMetric("nics_speed_"+i, nic.getSpeed());
      res.addMetric("nics_ipv4_"+i, Arrays.stream(nic.getIPv4addr()).collect(Collectors.joining(" ")));
      res.addMetric("nics_ipv6_"+i, Arrays.stream(nic.getIPv6addr()).collect(Collectors.joining(" ")));
      res.addMetric("nics_subnet_mask_"+i, Arrays.stream(nic.getSubnetMasks()).map(l -> ""+l).collect(Collectors.joining(" ")));
      i++;
    }
    res.addMetric("nics_count", i);
  }

  private void extractGraphicsCards(ResponseData res) {
    int i=0;
    for (GraphicsCard gc : extractor.getGraphicsCards()) {
      res.addMetric("graphicscards_name_"+i, gc.getName());
      res.addMetric("graphicscards_vendor_"+i, gc.getVendor());
      res.addMetric("graphicscards_device_id_"+i, gc.getDeviceId());
      res.addMetric("graphicscards_version_"+i, gc.getVersionInfo());
      i++;
    }
    res.addMetric("graphicscards_count", i);
  }

  private void extractDisksIo(ResponseData res) {
    if (disksStats == null) disksStats =  new HashMap<>();
    long tr=0,tw=0;
    for (HWDiskStore d : extractor.getDisks()) {
      String name = d.getName();
      DiskStats ds = disksStats.get(name);
      if (ds == null) {
        disksStats.put(d.getName(), ds = new DiskStats());
      } else {
        long r=d.getReadBytes()-ds.reads;
        long w=d.getWriteBytes()-ds.writes;
        res.addMetric("diskios_read_bytes_"+name, r);
        res.addMetric("diskios_write_bytes_"+name, w);
        tr+=r;
        tw+=w;
      }
      ds.reads = d.getReadBytes();
      ds.writes = d.getWriteBytes();
      res.addMetric("diskios_read_bytes", tr);
      res.addMetric("diskios_write_bytes", tw);
    }
  }

  private void extractDisks(ResponseData res) {
    for (HWDiskStore d : extractor.getDisks()) {
      String name = d.getName();
      res.addMetric("diskinfos_model_"+name, d.getModel());
      res.addMetric("diskinfos_serial_"+name, d.getSerial());
      res.addMetric("diskinfos_current_queue_length_"+name, d.getCurrentQueueLength());
      res.addMetric("diskinfos_read_bytes_"+name, d.getReadBytes());
      res.addMetric("diskinfos_reads_"+name, d.getReads());
      res.addMetric("diskinfos_size_"+name, d.getSize());
      res.addMetric("diskinfos_timestamp_"+name, d.getTimeStamp());
      res.addMetric("diskinfos_transfer_time_"+name, d.getTransferTime());
      res.addMetric("diskinfos_write_bytes_"+name, d.getWriteBytes());
      res.addMetric("diskinfos_writes_"+name, d.getWrites());
    }
    res.addMetric("diskinfos_names", extractor.getDisks().stream().map(di -> di.getName()).collect(Collectors.joining(",")));
  }

  private void extractDisplays(ResponseData res) {
    int i=0;
    for (Display d : extractor.getDisplays()) {
      res.addMetric("displays_digital_" + i, EdidUtil.isDigital(d.getEdid())?1:0);
      res.addMetric("displays_manufacturer_" + i, EdidUtil.getManufacturerID(d.getEdid()));
      res.addMetric("displays_digital_" + i, EdidUtil.isDigital(d.getEdid())?1:0);
      res.addMetric("displays_product_id_"+i, EdidUtil.getProductID(d.getEdid()));
      res.addMetric("displays_serial_"+i, EdidUtil.getSerialNo(d.getEdid()));
      res.addMetric("displays_version_"+i, EdidUtil.getVersion(d.getEdid()));
      res.addMetric("displays_week_"+i, EdidUtil.getWeek(d.getEdid()));
      res.addMetric("displays_year_"+i, EdidUtil.getYear(d.getEdid()));
      i++;
    }
    res.addMetric("displays_count", i);
  }

  private void extractCpuIdent(ResponseData res) {
    res.addMetric("cpuident_identifier", extractor.getProcessorIdentifier().getIdentifier());
    res.addMetric("cpuident_name", extractor.getProcessorIdentifier().getName());
    res.addMetric("cpuident_model", extractor.getProcessorIdentifier().getModel());
    res.addMetric("cpuident_processor_id", extractor.getProcessorIdentifier().getProcessorID());
    res.addMetric("cpuident_family", extractor.getProcessorIdentifier().getFamily());
    res.addMetric("cpuident_micro_arch", extractor.getProcessorIdentifier().getMicroarchitecture());
    res.addMetric("cpuident_vendor", extractor.getProcessorIdentifier().getVendor());
    res.addMetric("cpuident_vendor_freq", extractor.getProcessorIdentifier().getVendorFreq());
    res.addMetric("cpuident_stepping", extractor.getProcessorIdentifier().getStepping());
    res.addMetric("cpuident_64bits", extractor.getProcessorIdentifier().isCpu64bit()?1:0);

  }

  private void extractCpu(ResponseData res) {
    res.addMetric("cpu_context_switches", extractor.getProcessor().getContextSwitches());
    res.addMetric("cpu_interrupts", extractor.getProcessor().getInterrupts());
    res.addMetric("cpu_logical_processor_count", extractor.getProcessor().getLogicalProcessorCount());
    res.addMetric("cpu_physical_processor_count", extractor.getProcessor().getPhysicalProcessorCount());
    res.addMetric("cpu_max_freq", extractor.getProcessor().getMaxFreq());
    res.addMetric("cpu_physical_package_count", extractor.getProcessor().getPhysicalPackageCount());
    double[]la = extractor.getProcessor().getSystemLoadAverage(3);
    res.addMetric("cpu_load_1", la[0]);
    res.addMetric("cpu_load_2", la[1]);
    res.addMetric("cpu_load_3", la[2]);
    if (ticks != null) {
      res.addMetric("cpu_load", extractor.getProcessor().getSystemCpuLoadBetweenTicks(ticks));
    }
    ticks = extractor.getProcessor().getSystemCpuLoadTicks();
  }

  private void extractCpuByCore(ResponseData res) {
    if (ticksByCpu != null) {
      double[]bycore = extractor.getProcessor().getProcessorCpuLoadBetweenTicks(ticksByCpu);
      for (int i=0 ; i<bycore.length ; i++) {
        res.addMetric("cpubycore_load_" + i, bycore[i]);
      }
      res.addMetric("cpubycore_count", bycore.length);

    }
    ticksByCpu = extractor.getProcessor().getProcessorCpuLoadTicks();
    // Nothing interesting there
    // extractor.getProcessor().getLogicalProcessors().forEach(lp -> lp.?);
  }

  private void extractFirmware(ResponseData res) {
    res.addMetric("firmware_manufacturer", extractor.getFirmware().getManufacturer());
    res.addMetric("firmware_name", extractor.getFirmware().getName());
    res.addMetric("firmware_description", extractor.getFirmware().getDescription());
    res.addMetric("firmware_version", extractor.getFirmware().getVersion());
    res.addMetric("firmware_release_date", extractor.getFirmware().getReleaseDate());
  }

  private void extractBaseboard(ResponseData res) {
    res.addMetric("baseboard_manufacturer", extractor.getBaseboard().getManufacturer());
    res.addMetric("baseboard_model", extractor.getBaseboard().getModel());
    res.addMetric("baseboard_serial_number", extractor.getBaseboard().getSerialNumber());
    res.addMetric("baseboard_version", extractor.getBaseboard().getVersion());
  }

  private void extractVirtualMemory(ResponseData res) {
    res.addMetric("virtualmemory_max", extractor.getVirtualMemory().getVirtualMax());
    res.addMetric("virtualmemory_in_use", extractor.getVirtualMemory().getVirtualInUse());
    res.addMetric("virtualmemory_swap_in", extractor.getVirtualMemory().getSwapPagesIn());
    res.addMetric("virtualmemory_swap_out", extractor.getVirtualMemory().getSwapPagesOut());
    res.addMetric("virtualmemory_swap_total", extractor.getVirtualMemory().getSwapTotal());
    res.addMetric("virtualmemory_swap_used", extractor.getVirtualMemory().getSwapUsed());
  }

  private void extractPhysicalMemory(ResponseData res) {
    int i=0;
    for (PhysicalMemory ps : extractor.getPhysicalMemory()) {
      res.addMetric("physicalmemory_name_"+i, ps.getBankLabel());
      res.addMetric("physicalmemory_capacity_"+i, ps.getCapacity());
      res.addMetric("physicalmemory_type_"+i, ps.getMemoryType());
      res.addMetric("physicalmemory_clock_"+i, ps.getClockSpeed());
      res.addMetric("physicalmemory_manufacturer_"+i, ps.getManufacturer());
      i++;
    }
    res.addMetric("physicalmemory_nb", i);
  }

  private void extractComputer(ResponseData res) {
    res.addMetric("computer_manufacturer", extractor.getComputerSystem().getManufacturer());
    res.addMetric("computer_UUID", extractor.getComputerSystem().getHardwareUUID());
    res.addMetric("computer_model", extractor.getComputerSystem().getModel());
    res.addMetric("computer_serial_number", extractor.getComputerSystem().getSerialNumber());
  }

  private void extractMemory(ResponseData res) {
    res.addMetric("memory_available", extractor.getMemory().getAvailable());
    res.addMetric("memory_total", extractor.getMemory().getTotal());
  }

  private void extractSensors(ResponseData res) {
    int i=0;
    for (int speed : extractor.getSensors().getFanSpeeds()) {
      res.addMetric("sensors_fan_speed_"+i++, speed);
    }
    res.addMetric("sensors_fan_count", i);
    res.addMetric("sensors_cputemp", extractor.getSensors().getCpuTemperature());
    res.addMetric("sensors_cpuvolt", extractor.getSensors().getCpuVoltage());
  }

  @Override
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void setConfig(Map<String, String> config) {
  }

  @Override
  public void writeHtmlTemplate(Writer writer) throws IOException {
  }
}
