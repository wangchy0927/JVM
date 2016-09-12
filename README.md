该程序是为了满足zabbix监控jvm的参数而写的，现在能够获取远程jvm的堆、栈、线程和加载类信息这四种数据，运行该程序需要传递两个参数:远程jvm的ip和端口。

    if (args.length < 2) {           
    			System.out.println("please input jmx ip and port!");
    			System.exit(0);
    		}
