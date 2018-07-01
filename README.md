## RAMP 基于微信公众号的关系聚合管理平台

### 公众号托管
公众号分为两类：B类型、C类型。
>
    B类型: 可托管多个不同的公众号。
    托管流程：
        1、调用托管接口 POST /api/admin/wxapp/trust
        2、成功后返回托管的接口地址
        3、在微信公众号平台中 开发-接口配置-服务器配置中填写上步返回的接口地址。
        4、验证通过后即表示该公众号托管成功。
    C类型: 平台允许托管C类型的公众号和C类型的小程序。
    
### 二维码管理
二维码是指平台为C类型公众号创建的带有自定义场景值的二维码。
作为B公众号用户的身份标识，同时也是作为B用户、C用户建立关联关系的桥梁。
>
    二维码管理主要分两种情况：
        1、创建二维码： 调用接口 POST /api/admin/qrcode
           创建成功后平台下载二维码到服务器。
        2、分配给B用户: Admin审核B用户的注册申请并通过后，平台为B用户分配一个二维码。
> 
    二维码绑定机制重构
    
        1、多个C的情况下，一个B用户可以同时拥有多个C公众号二维码的绑定。
        2、已被绑定的二维码不可再分配给其他人。
### B用户注册
关注了B公众号的用户，可以申请注册，注册审核通过后即为高级用户。未注册的用户则为B公众号的普通用户。
> 
    B用户的注册流程：
        1、发起注册申请：向公众号发送: "注册" 关键字，平台提示输入手机号。
        2、向公众号发送手机号。
        3、平台发送短信验证码给用户，并提示用户输入验证码。
        4、用户发送验证码，验证正确后，平台提交注册申请。
        5、Admin审核B用户的注册申请并通过后，平台为B用户分配二维码。

### B、C用户关系建立
平台的目标是提供B、C两类用户之间的关系管理。以及建立关联关系后的互动。
两者建立关系的关键是扫描二维码。
> 
    微信用户在扫描带有特定场景值的二维码后，平台根据该用户的openid、二维码场景值进行识别并建立两者的关系。
    其流程为：
        1、平台识别出扫码用户的openid和二维码携带的场景值。
        2、根据场景值匹配绑定的B用户。
        3、扫码用户未关注C公众号的情况下，自动关注公众号，并建立其与B用户的关联关系。
        4、扫码用户已关注C公众号的情况下，建立其与B用户的关联关系。
        
    