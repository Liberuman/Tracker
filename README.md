Tracker是一个无埋点SDK，开发者只需要在Application中初始化即可，不需要额外添加任何埋点代码。

#### 原理

Tracker采用了全局监听的方式，对Activity，Fragment的生命周期，View的点击事件进行监听，，当有事件产生时，根据从服务器获取的埋点配置信息（初始化时获取），
将需要的埋点数据保存起来，到上传时机时将其上传到服务器。

对于埋点配置信息的收集，Tracker采用了长连接的方式，在APP上传到应用市场前，将Tracker的埋点数据上传时机设置为实时上传，即可将生成的所有事件实时上传到服务器，
服务器在收到事件后，手动将其添加到配置信息列表中，即可完成配置信息的收集。

#### 使用

在Application的onCreate中进行初始化即可：

    TrackerConfiguration configuration = new TrackerConfiguration()
            // 开启log
            .openLog(true)
            // 设置日志的上传策略
            .setUploadCategory(UPLOAD_CATEGORY.REAL_TIME.getValue())
            // 设置获取埋点配置信息列表的URL
            .setConfigUrl("http://m.baidu.com")
            // 设置实时上传日志信息的IP和端口
            .setHostName("127.0.0.1")
            .setHostPort(10001)
            // 设置提交新设备信息的URL
            .setNewDeviceUrl("http://m.baidu.com")
            // 设置需要提交的新设备信息
            .setDeviceInfo("?deviceId=123456&osVersion=8.0")
            // 设置埋点信息上传的URL
            .setUploadUrl("http://m.baidu.com")
            // 设置上传埋点信息的公共参数
            .setCommonParameter("?channel=mi&version=1.0");
    Tracker.getInstance().init(this, configuration);

对于新设备的信息和公共参数，默认提供了包名，渠道，版本号，设备ID，手机品牌，手机系统版本，但在实际开发中，
需要的参数可能有所差异，所以提供了自定义的功能，只需要将需要的参数以URL参数的格式进行拼接即可。

> 在发布版本之前，将上传策略设置成Constants.UPLOAD_CATEGORY.REAL_TIME收集埋点配置信息，APP上线时务必将数据上传策略改成其他的，避免耗电。

对于埋点数据的上传，提供了以下策略：

    REAL_TIME(0),           // 实时传输，用于收集配置信息
	NEXT_LAUNCH(-1),        // 下次启动时上传
	NEXT_15_MINUTER(15),    // 每15分钟上传一次
	NEXT_30_MINUTER(30),    // 每30分钟上传一次
	NEXT_KNOWN_MINUTER(-1); // 使用服务器下发的上传策略（间隔时间由服务器决定）

#### 说明

目前此SDK只集成了新设备信息，页面(Activity/Fragment)的停留事件，View的点击事件的统计，对于其他的交互事件还未集成，一些细节方面也还有待改进，随后会进一步完善。