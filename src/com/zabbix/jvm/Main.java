package com.zabbix.jvm;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.management.OperatingSystemMXBean;

public class Main {

	/**
	 * 输入两个参数:要监控的jvm的ip和jmx端口
	 * */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("please input jmx ip and port!");
			System.exit(0);
		}
		Main main = new Main();
		String ip = args[0];
		String port = args[1];
		JMXServiceURL address = null;
		JMXConnector connector = null;
		try {
			//拼接jmx连接串
			address = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
					+ ":" + port + "/jmxrmi");
			connector = JMXConnectorFactory.connect(address);
			MBeanServerConnection mbs = connector.getMBeanServerConnection();
			String result = main.getClasses(mbs)+":"+main.getMemory(mbs)+":"+main.getThread(mbs);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getCPU(MBeanServerConnection mbs) throws Exception {
		RuntimeMXBean runtimeMBean = ManagementFactory
				.newPlatformMXBeanProxy(mbs,
						ManagementFactory.RUNTIME_MXBEAN_NAME,
						RuntimeMXBean.class);
		OperatingSystemMXBean operatingSystemMBean = ManagementFactory
				.newPlatformMXBeanProxy(mbs,
						ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
						com.sun.management.OperatingSystemMXBean.class);
		int nCPUs = operatingSystemMBean.getAvailableProcessors();
		if (runtimeMBean != null && operatingSystemMBean != null) {
			long prevUpTime = runtimeMBean.getUptime();
			long prevProcessCpuTime = operatingSystemMBean.getProcessCpuTime();
			Thread.sleep(100);
			long uptime = runtimeMBean.getUptime();
			long processCpuTime = operatingSystemMBean.getProcessCpuTime();
			long elapsedCpu = processCpuTime - prevProcessCpuTime;
			long elaspedTime = uptime - prevUpTime;
			float cpuUsage = Math.min(99F, elapsedCpu
					/ (elaspedTime * 10000F * nCPUs));
			System.out.println(cpuUsage);
		}
	}

	public String getClasses(MBeanServerConnection mbs) throws IOException {
		ClassLoadingMXBean cbean = ManagementFactory.newPlatformMXBeanProxy(
				mbs, ManagementFactory.CLASS_LOADING_MXBEAN_NAME,
				ClassLoadingMXBean.class);
		return ("ClassLoadingMXBean:" + "getLoadedClassCount:"
				+ cbean.getLoadedClassCount() + ":" + "getUnloadedClassCount:" + cbean
				.getUnloadedClassCount());

	}

	public String getMemory(MBeanServerConnection mbs) throws IOException {
		MemoryMXBean memory = ManagementFactory.newPlatformMXBeanProxy(mbs,
				ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
		MemoryUsage usage = memory.getHeapMemoryUsage();
		MemoryUsage nonUsage = memory.getNonHeapMemoryUsage();
		return ("MemoryUsage:" + "getInit:" + usage.getInit() + ":" + "getMax:"
				+ usage.getMax() + ":" + "getUsed:" + usage.getUsed())
				+ (":MemoryNonUsage:" + "getInit:" + nonUsage.getInit() + ":"
						+ "getMax:" + nonUsage.getMax() + ":" + "getUsed:" + nonUsage
						.getUsed());

	}

	public String getThread(MBeanServerConnection mbs) throws IOException {
		ThreadMXBean threadBean = ManagementFactory.newPlatformMXBeanProxy(mbs,
				ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);

		return ("ThreadMXBean:" + "getDaemonThreadCount:"
				+ threadBean.getDaemonThreadCount() + ":"
				+ "getPeakThreadCount:" + threadBean.getPeakThreadCount() + ":"
				+ "getTotalStartedThreadCount:" + threadBean
				.getTotalStartedThreadCount());

	}

}
