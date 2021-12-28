# conkw documentation - OshiGrabber

This is the system grabber using the OSHI API. This API is built on top of JNA and can drill down pretty deep in the host system. It should be noted that it is much more expensive to use than [ProcGrabber](GRABBER_PROC_GRABBER.md), but it works on non-linux systems.

This documentation will not drill down on OSHI and its system representation. For more details on that, please refer to [the OSHI documentation](https://github.com/oshi/oshi).

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.oshi.OshiGrabber`
* *Default instance name:* `oshi`

## Use cases

* You want to monitor system metrics out of your Mac, Linux or Windows machine. That's CPU, HDD, SSD, Network, battery, etc.

## Configuration

```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.oshi.OshiGrabber",
  "name":"oshi-processes",
  "parameters": {
    "toExtract":["processes"],
    "detailedDataDelay":"5s",
    "staticDataDelay":"86400"
  }
}
```

* `toExtract`: you can specify one or more class(es) of data to extract. This will considerably improve performance if you don't need some of the most expensive extractions on all the time (See `processes` and `services` below)
* `detailedDataDelay`: The delay at which the most expensive data is extracted, instead of once every second. That applies to `processes` and `services` below. Default is 5 seconds.
* `staticDataDelay`: The delay at which the "static" data is extracted. This is serial numbers, makes and models of various pieces of hardware on your computer. Default is one day.
* [See here for the delay format](CONCEPTS.md)

The default configuration includes three of these grabbers, one configured for processes, one for services and one for the rest. Note how having three instances allows to benefit from the whole range of metrics while only consuming resources for the ones you actually have a dashboard open. 

## Possible extractions

### sensors
Metrics:

* `num.sensors_fan_speed_*` The fan speed in RPM for the fan number `*`.
* `num.sensors_fan_count` The number of fans reported.
* `num.sensors_cputemp` The CPU temperature in degree Celsuis.
* `num.sensors_cpuvolt` The CPu input coltage, in Volts.

### memory
Metrics, in bytes:

* `num.memory_available` The total memory available.
* `num.memory_total` The total memory in the system.
* `num.memory_in_use` The memory in use of the system.
* `num.memory_page_size` The page size.

### physicalmemory
Metrics:

* `num.physicalmemory_nb` The number of physical memory modules.

For each module number *i* (starting at zero):

* `str.physicalmemory_name_i` the name of the module.
* `num.physicalmemory_capacity_i` the capacity in bytes.
* `str.physicalmemory_type_i` The type of memory module (ex: `DDR3`)
* `num.physicalmemory_clock_i` The clock rate of the memory module, in Hz.
* `str.physicalmemory_manufacturer_i` The manufacturer of the module.

### virtualmemory
Metrics:

* `num.virtualmemory_max` The max total virtual memory, in bytes.
* `num.virtualmemory_in_use` The virtual memory in use, in bytes.
* `num.virtualmemory_swap_in` The number of swap pages ins.
* `num.virtualmemory_swap_out` The number of swap pages out.
* `num.virtualmemory_swap_total` The total swap space, in bytes.
* `num.virtualmemory_swap_used` The used swap space, in bytes.

### computer
Metrics:

* `str.computer_manufacturer` The computer manufacturer.
* `str.computer_UUID` The computer UUID.
* `str.computer_model` The computer model.
* `str.computer_serial_number` The computer serial number.


### baseboard
Metrics:

* `str.baseboard_manufacturer` The baseboard manufacturer.
* `str.baseboard_model` The baseboard model.
* `str.baseboard_serial_number` The baseboard serial number.
* `str.baseboard_version` The baseboard version.


### firmware
Metrics:

* `str.firmware_manufacturer` The firmware manufacturer.
* `str.firmware_name` The firmware name.
* `str.firmware_description` The firmware description.
* `str.firmware_version` The firmware version.
* `str.firmware_release_date` The firmware release date.


### cpu
Metrics:

* `num.cpu_context_switches` The number of context switches that have occured.
* `num.cpu_interrupts` The number of interrupts that have occured.
* `num.cpu_logical_processor_count` The number of logical processors.
* `num.cpu_physical_processor_count` The number of cores.
* `num.cpu_max_freq` The maximum frequency of the processord, in Hz.
* `num.cpu_physical_package_count` The number of physical processors.
* `num.cpu_load_1` The load average on the last minute.
* `num.cpu_load_2` The load average on the last 5 minutes.
* `num.cpu_load_3` The load average on the last 15 minutes.
* `num.cpu_load` The cpu usage over the last second, in percentage between 0 and 1.


### cpubycore
Metrics:

* `num.cpubycore_count` The number of cores, to iterate over the following metrics:

For each core *i* :

* `num.cpubycore_load_i` The usage of this core over the last second, in percentage between 0 and 1.


### cpuident
Metrics:

* `str.cpuident_identifier` Name of the processor.
* `str.cpuident_name` Another name for the processor.
* `str.cpuident_model` Processor model.
* `str.cpuident_processor_id` Processor id.
* `str.cpuident_family` Processor family.
* `str.cpuident_micro_arch` Processor microarchitecture.
* `str.cpuident_vendor` Processor vendor.
* `num.cpuident_vendor_freq` Processor frequency, in Hz.
* `str.cpuident_stepping` Processor stepping.
* `num.cpuident_64bits` 1 if yes, 0 if not.


### displays
Metrics:

* `num.displays_count` The number of displays, to iterate over the following metrics:

For each display *i* (from 0 to `num.displays_count`-1):

* `num.displays_digital_i` 1 if the display is digital, or else 0.
* `str.displays_manufacturer_i` The display manufacturer.
* `str.displays_product_id_i` The display product id.
* `str.displays_serial_i` The display serial number.
* `str.displays_version_i` The display version.
* `num.displays_week_i` The display release week.
* `num.displays_year_i` The display release year.

### disksio
Metrics:

* `num.diskios_read_bytes` The number of bytes read during the last second on all disks, in bytes.
* `num.diskios_write_bytes` The number of bytes written during the last second on all disks, in bytes.
* Auto max: `num.max$diskios_read_bytes` The maximum number observed for this value.
* Auto max: `num.max$diskios_write_bytes` The maximum number observed for this value.
* `num.diskios_disks` The list of disks being monitored. For example: `/dev/sdd,/dev/nvme0n1`

For each one of the disks *d* :

* `num.diskios_read_bytes_d` The number of bytes read during the last second, in bytes.
* `num.diskios_write_bytes_d` The number of bytes written during the last second, in bytes.
* Auto max: `num.max$diskios_read_bytes_d` The maximum number observed for this value.
* Auto max: `num.max$diskios_write_bytes_d` The maximum number observed for this value.


### disksinfos
Metrics:

* `num.diskinfos_names` The list of disks being monitored. For example: `/dev/sdd,/dev/nvme0n1`

For each one of the disks *d* :

* `str.diskinfos_model_d` the disk model.
* `str.diskinfos_serial_d` The disk serial number
* `num.diskinfos_current_queue_length_d` The disk queue length.
* `num.diskinfos_read_bytes_d` The number of bytes read.
* `num.diskinfos_reads_d` The number of reads.
* `num.diskinfos_size_d` The disk size, in bytes.
* `num.diskinfos_timestamp_d` The time this disk's statistics were updated (timestamp in milliseconds)
* `num.diskinfos_transfer_time_d` Time spent reading or writing, in milliseconds.
* `num.diskinfos_write_bytes_d` The number of bytes written.
* `num.diskinfos_writes_d` The number of writes.

### graphicscards
Metrics:

* `num.graphicscards_count` The number of graphics cards.

For each one of those graphics card *i*, starting at zero:

* `graphicscards_name_i` The name
* `graphicscards_vendor_i` The vendor
* `graphicscards_device_id_i` The ID
* `graphicscards_version_i` The version.


### nics
Metrics:

* `num.nics_count` The number of NICs

For each one of those nics *i*, starting at zero:

* `str.nics_name_i` The name
* `str.nics_display_name_i` Another name 
* `str.nics_macaddr_i` The Mac address
* `num.nics_bytes_recv_i` Number of bytes received.
* `num.nics_bytes_sent_i` Number of bytes sent.
* `num.nics_if_type_i` The type
* `str.nics_speed_i` The maximum speed, in bytes per seconds.
* `str.nics_ipv4_i` The list of ipv4 addresses.
* `str.nics_ipv6_i` The list of ipv6 addresses.
* `str.nics_subnet_mask_i` The list of subnet masks.

### netbw
Metrics:

* `num.netbw_count` The number of network interfaces.
* `num.netbw_in` The total bytes in since the last second.
* `num.netbw_out` The total bytes out since the last second.
* Auto max: `num.max$netbw_in` The maximum number observed for this value.
* Auto max: `num.max$netbw_out` The maximum number observed for this value.
* `num.netbw_speed` The sum of all interfaces speed. 

For each one of those interfaces *i*, starting at zero:

* `num.netbw_in_i` The total bytes in since the last second.
* `num.netbw_out_i` The total bytes out since the last second.
* Auto max: `num.max$netbw_in_i` The maximum number observed for this value.
* Auto max: `num.max$netbw_out_i` The maximum number observed for this value.
* `num.netbw_speed_i` The speed of the interface, in bytes per second, or -1 if not available.

### battery
Metrics are extracted for the first battery only:

* `num.battery_remaining_prc` PErcentage left in the battery, between 0 and 1.
* `num.battery_plugged` 1 if the battery is plugged in, otherwise 0.

### psus
Metrics:

* `num.psus_count` the number of PSUs in the system.

For each one PSU *i* :

* `str.psus_name_i` The name
* `str.psus_manufacturer_i` The manufacturer.
* `str.psus_serial_number_i` The serial number.
* `str.psus_chemistry_i` The chrmistry
* `num.psus_device_name_i` Another name
* `num.psus_amperage_i` The amperage, in amps.
* `num.psus_capacity_units_i` The unit of the capacity.
* `num.psus_current_capacity_i` The current capacity.
* `num.psus_cycle_count_i` The cycle count.
* `num.psus_design_capacity_i` The designed capacity.
* `str.psus_manufacture_date_i` The manufacturing year
* `num.psus_power_usage_rate_i` The power usage rate.
* `num.psus_remaining_capacity_percent_i` The remaining capacity, in percentage, between 0 and 1.
* `num.psus_temperature_i` The temperature
* `num.psus_time_remaining_estimated_i` The estimated time remaining in seconds (reported by the OS)
* `num.psus_voltage_i` The voltage, in volts.
* `num.psus_time_remaining_instant_i` The estimated time remaining in seconds (reported by the battery)
* `num.psus_in_charge_i` 1 if in charge, or 0 if not.
* `num.psus_in_discharge_i` 1 if in discharge, or 0 if not.
* `num.psus_power_online_i` 1 if in online, or 0 if not.


### soundcards
Metrics:

* `num.soundcards_count` The number of soundcards

For each one soundcard *i* :

* `str.soundcards_name_i`
* `str.soundcards_codec_i`
* `str.soundcards_driver_version_i`

### usb
List all USB devices in the system. Metrics:

* `num.usb_count` The number of USB devices.

For each one device *i* :

* `str.usb_name_i`
* `str.usb_serial_number_i`
* `str.usb_vendor_i`
* `str.usb_vendor_id_i`
* `str.usb_product_id_i`
* `str.usb_unique_device_id_i`

### os
Metrics:

* `str.os_manufacturer`
* `str.os_family`
* `num.os_bitness`
* `num.os_process_count` The number of running processes.
* `num.os_process_id` The process ID of conkw.
* `num.os_system_boot_time` Unix time of boot.
* `num.os_system_uptime` The system uptime.
* `num.os_thread_count` The number of threads running.
* `str.os_version`
* `str.os_build_number`
* `str.os_code_name`
* `num.os_max_file_descriptors` The maximum number of file descriptors.
* `num.os_open_file_descriptors` The number of opened file descriptors.


### filestores
Metrics:

* `num.filestores_count` The number of filestores.

For each filestore *i* :

* `str.filestores_name_i` 
* `str.filestores_description_i` 
* `str.filestores_type_i` Usually the filesystem used.
* `str.filestores_label_i` 
* `str.filestores_mount_i` The mount point.
* `str.filestores_logical_volume_i` 
* `str.filestores_options_i` 
* `str.filestores_uuid_i` 
* `str.filestores_volume_i` The device, eg `/dev/sda1`
* `num.filestores_free_space_i` The free space on the device, in bytes.
* `num.filestores_total_space_i` The total space on the device, in bytes.
* `num.filestores_usable_space_i` The usable space on the device, in bytes.


### tcpv4
Metrics:

* `num.tcpv4_connection_failures` 
* `num.tcpv4_connections_active` 
* `num.tcpv4_connections_established` 
* `num.tcpv4_out_resets` 
* `num.tcpv4_connections_passive` 
* `num.tcpv4_connections_reset` 
* `num.tcpv4_in_errors` 
* `num.tcpv4_segments_received` 
* `num.tcpv4_segments_retransmitted` 
* `num.tcpv4_segments_sent` 


### tcpv6
Metrics:

* `num.tcpv6_connection_failures` 
* `num.tcpv6_connections_active` 
* `num.tcpv6_connections_established` 
* `num.tcpv6_out_resets` 
* `num.tcpv6_connections_passive` 
* `num.tcpv6_connections_reset` 
* `num.tcpv6_in_errors` 
* `num.tcpv6_segments_received` 
* `num.tcpv6_segments_retransmitted` 
* `num.tcpv6_segments_sent` 


### udpv4
Metrics:

* `num.udpv4_datagrams_no_port`
* `num.udpv4_datagrams_received`
* `num.udpv4_datagrams_sent`
* `num.udpv4_datagrams_received_errors`


### udpv6
Metrics:

* `num.udpv6_datagrams_no_port`
* `num.udpv6_datagrams_received`
* `num.udpv6_datagrams_sent`
* `num.udpv6_datagrams_received_errors`

### sessions
Metrics:

* `num.sessions_count` The number of user sessions

For each session *i* :

* `str.sessions_host_i`
* `str.sessions_login_time_i` The time at which the user logged in, as a timestamp.
* `str.sessions_user_name_i`
* `str.sessions_terminal_device_i`


### shortsessions
A summarized view of the sessions by user. Metrics:

* `num.shortsessions_count` The number of users having a session.

For each item *i* :

* `str.shortsessions_name_i` The username.
* `num.shortsessions_nbs_i` The number of sessions opened.

### netp
The network parameters. Metrics:

* `str.netp_domain_name` 
* `str.netp_host_name` 
* `str.netp_ipv4_default_gateway` 
* `str.netp_ipv6_default_gateway` 
* `str.netp_dns` The list of DNS defined.


### processes
*Note* This extraction is particularly expensive on the host system. You may not want to leave it running for long periods of time.

Metrics:

* `num.processes_count` the number of processes.

For each process *p*:

* `num.processes_affinity_mask_p`
* `num.processes_bitness_p`
* `str.processes_name_p` The short name.
* `num.processes_bytes_read_p`
* `num.processes_bytes_written_p`
* `str.processes_command_line_p`
* `str.processes_current_working_directory_p`
* `str.processes_group_p`
* `str.processes_group_id_p`
* `num.processes_kernel_time_p`
* `num.processes_major_faults_p`
* `num.processes_minor_faults_p`
* `num.processes_open_files_p`
* `num.processes_parent_process_id_p`
* `str.processes_path_p`
* `num.processes_priority_p`
* `num.processes_process_cpu_load_instant_p`
* `num.processes_process_cpu_load_cumulative_p`
* `num.processes_process_id_p`
* `num.processes_resident_set_size_p`
* `num.processes_start_time_p` The time the process was started, as a timestamp.
* `num.processes_thread_count_p`
* `num.processes_up_time_p` The system up time.
* `str.processes_user_p`
* `str.processes_user_id_p`
* `num.processes_user_time_p`
* `num.processes_virtual_size_p`
* `str.processes_state_p`

### services
*Note* This extraction is particularly expensive on the host system. You may not want to leave it running for long periods of time.

Metrics:

* `num.services_count` The number of services.

For each service *s* :

* `str.services_name_s`
* `num.services_process_id_s`
* `str.services_state_s`

