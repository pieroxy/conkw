package net.pieroxy.conkw.webapp.grabbers.oshi;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;

import java.util.List;

public class OSHIExtractor {
  private SystemInfo si = new SystemInfo();
  private HardwareAbstractionLayer hardware;
  private Sensors sensors;
  private GlobalMemory memory;
  private OperatingSystem os;
  private ComputerSystem computerSystem;
  private CentralProcessor processor;
  private List<UsbDevice> usbDevices;
  private List<SoundCard> soundCards;
  private List<GraphicsCard> graphicsCards;
  private List<Display> displays;
  private VirtualMemory virtualMemory;
  private List<PhysicalMemory> physicalMemory;
  private Baseboard baseBoard;
  private Firmware firmware;
  private CentralProcessor.ProcessorIdentifier processorIdent;
  private OperatingSystem.OSVersionInfo osvi;
  private FileSystem osfs;
  private List<OSFileStore> osfstores;
  private InternetProtocolStats.TcpStats ipv4;
  private InternetProtocolStats.TcpStats ipv6;
  private InternetProtocolStats netstats;
  private InternetProtocolStats.UdpStats udpv4;
  private InternetProtocolStats.UdpStats udpv6;
  private List<OSSession> sessions;
  private NetworkParams netp;

  public HardwareAbstractionLayer getHardware() {
    if (hardware==null) hardware = si.getHardware();
    return hardware;
  }


  public OperatingSystem getOperatingSystem() {
    if (os==null) os = si.getOperatingSystem();
    return os;
  }

  public List<OSProcess> getProcesses() {
    return getOperatingSystem().getProcesses();
  }

  public OSService[] getServices() {
    return getOperatingSystem().getServices();
  }

  public OperatingSystem.OSVersionInfo getVersionInfo() {
    if (osvi==null) osvi = getOperatingSystem().getVersionInfo();
    return osvi;
  }

  public NetworkParams getNetworkParams() {
    if (netp==null) netp = getOperatingSystem().getNetworkParams();
    return netp;
  }

  public FileSystem getFileSystem() {
    if (osfs==null) osfs = getOperatingSystem().getFileSystem();
    return osfs;
  }

  public List<OSSession> getSessions() {
    if (sessions==null) sessions = getOperatingSystem().getSessions();
    return sessions;
  }

  public InternetProtocolStats getInternetProtocolStats() {
    if (netstats==null) netstats = getOperatingSystem().getInternetProtocolStats();
    return netstats;
  }

  public InternetProtocolStats.TcpStats getNetStatsIpv4() {
    if (ipv4==null) ipv4 = getInternetProtocolStats().getTCPv4Stats();
    return ipv4;
  }

  public InternetProtocolStats.TcpStats getNetStatsIpv6() {
    if (ipv6==null) ipv6 = getInternetProtocolStats().getTCPv6Stats();
    return ipv6;
  }

  public InternetProtocolStats.UdpStats getNetStatsUdpv4() {
    if (udpv4==null) udpv4 = getInternetProtocolStats().getUDPv4Stats();
    return udpv4;
  }

  public InternetProtocolStats.UdpStats getNetStatsUdpv6() {
    if (udpv6==null) udpv6 = getInternetProtocolStats().getUDPv6Stats();
    return udpv6;
  }

  public List<OSFileStore> getFileStores() {
    if (osfstores==null) osfstores = getFileSystem().getFileStores();
    return osfstores;
  }

  public Sensors getSensors() {
    if (sensors==null) sensors = getHardware().getSensors();
    return sensors;
  }

  public GlobalMemory getMemory() {
    if (memory == null) memory = getHardware().getMemory();
    return memory;
  }

  public List<PhysicalMemory> getPhysicalMemory() {
    if (physicalMemory == null) physicalMemory = getMemory().getPhysicalMemory();
    return physicalMemory;
  }

  public VirtualMemory getVirtualMemory() {
    if (virtualMemory == null) virtualMemory = getMemory().getVirtualMemory();
    return virtualMemory;
  }

  public ComputerSystem getComputerSystem() {
    if (computerSystem == null) computerSystem = getHardware().getComputerSystem();
    return computerSystem;
  }

  public Baseboard getBaseboard() {
    if (baseBoard == null) baseBoard = getComputerSystem().getBaseboard();
    return baseBoard;
  }

  public Firmware getFirmware() {
    if (firmware == null) firmware = getComputerSystem().getFirmware();
    return firmware;
  }

  public CentralProcessor getProcessor() {
    if (processor == null) processor = getHardware().getProcessor();
    return processor;
  }

  public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
    if (processorIdent == null) processorIdent = getProcessor().getProcessorIdentifier();
    return processorIdent;
  }

  public List<UsbDevice> getUsbDevices() {
    if (usbDevices == null) usbDevices = getHardware().getUsbDevices(false);
    return usbDevices;
  }

  public List<HWDiskStore> getDisks() {
    return getHardware().getDiskStores();
  }

  public List<Display> getDisplays() {
    if (displays == null) displays = getHardware().getDisplays();
    return displays;
  }

  public List<GraphicsCard> getGraphicsCards() {
    if (graphicsCards == null) graphicsCards = getHardware().getGraphicsCards();
    return graphicsCards;
  }

  public List<NetworkIF> getNICs() {
    return getHardware().getNetworkIFs();
  }

  public List<PowerSource> getPowerSources() {
    return getHardware().getPowerSources();
  }

  public List<SoundCard> getSoundCards() {
    if (soundCards == null) soundCards = getHardware().getSoundCards();
    return soundCards;
  }
}
