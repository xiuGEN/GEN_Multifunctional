package com.xiuGEN;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.xiuGEN.mapper.*;
import com.xiuGEN.pojo.FileInfo;
import com.xiuGEN.pojo.Friend;
import com.xiuGEN.pojo.User;
import com.xiuGEN.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;


/*
* 在spring boot单元测试的时候会遇到很多问题，我在使用websocket的时候会运行测试类，
* 报错： Error creating bean with name 'serverEndpointExporter'
* defined in class path resource [com/Jacklin/config/WebSocketConfig.class] ，
* 我这里引入了注解@ServerEndPoint：
*
* 解决的方式有两种：

第一种方式：去掉测试类的@RunWith(SpringRunner.class)，去掉即可,
*       但是这种方式会有局限，比如下方你要@Authwired一个类的时候会报错，
*           我这里不可以，根据你的代码情况。
第二种方式：在SpringBootTest后加上：webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT，
*           意思是创建Web应用程序上下文（基于响应或基于servlet），
* 原因:websocket是需要依赖tomcat等容器的启动所以在测试过程中我们要真正的启动一个tomcat作为容器。
*
*
*

* */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenMultifunctionalApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Test
    void DemoUserMapper() {
        List<User> users = userMapper.selUsers();
        System.out.println(users);
    }
    @Test
    void DemoExist(){
        User uemail = userMapper.selExist("uemail", "87032919@qq.com");
        System.out.println("查询结果为:" +uemail);
    }
    @Test
    void TestUUID(){
        UUID uuid =UUID.randomUUID();
        System.out.println(uuid.toString());
        String replace = uuid.toString().replace("-", "");
        String replace2 = uuid.toString().replace("-", "");
        System.out.println(replace.length());
        String str ="GEN";
        String all= replace+str+replace2;
        String str2=all.substring(replace.length(),replace.length()+3);
        System.out.println(str2);
    }

	/*
	 * @Test void TestStringBuffer() throws IOException{
	 * 
	 * File file = new File("C:\\Users\\GEN\\Desktop\\test","test.test1"); boolean
	 * newFile = file.createNewFile(); System.out.println(newFile);
	 * if(!file.exists()) { System.out.println("tests"); file.mkdirs(); } }
	 */
    @Autowired
    private CapacityMapper capacityMapper;
    @Test
    void testCapacity(){
        System.out.println(capacityMapper.selCapacityByUid(1));
    }


    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Test
    void testFilInfo(){
        List<FileInfo> fileInfos = fileInfoMapper.selFilesByOther(3);
        System.out.println(fileInfos);
    }

    @Autowired
    private TypeMapper TypeMapper;
    @Test
    void testTypeMapper(){
        String fileInfos = TypeMapper.selTypeBySuffix(".jpg");
        System.out.println(fileInfos);
    }

	/*
	 * @Test void testFile(){ File file =new File(
	 * "C:\\Users\\GEN\\IdeaProjects\\GEN_multifunctional\\src\\main\\resources\\uploadFile\\1","AaronSmith、Luvli - Dancin (Krono Remix).mp3"
	 * ); System.out.println(file.exists()); }
	 */

    @Test
    void testDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date =new Date(System.currentTimeMillis());

        String format = simpleDateFormat.format(date);
        System.out.println(format);
    }
    
    @Test
    void testClone() {
    	User s = new User();
    	s.setUaddress("sdfsdf");
    	s.setUbirthdate("sdfsdf");
    	s.setUemail("sdf");
    	User clone =new User();
    	 BeanUtil.copyProperties(s, clone, false);
    	 System.out.println(" s: "+s);
    	 System.out.println(" clone ="+clone);
    	 System.out.println(RandomUtil.randomString(5));
    }
    @Test
    void testDigest(){
        String t= "12345678";
        String test=new String (DigestUtil.md5Hex(t));
        System.out.println("grny:  "+test);
    }
    @Autowired
    private MessageService chatService;
   /* @Test
    void testChatRecordsMapper(){
        List<Chatrecord> messages = chatService.getMessages(1, 0);
        List<Chatrecord> messagesToOnePerson = chatService.getMessagesToOnePerson(9, 15);
        System.out.println(messagesToOnePerson);
    }*/

    /*测试friendMapping*/
    @Autowired
    private FriendtableMapper friendtableMapper;
    @Test
    void testFriendtableMapper(){
        int i = friendtableMapper.insFriend(1, 2);
        List<Friend> friends = friendtableMapper.selFriends(9);
        System.out.println(friends);
    }

    @Test
    void testlastindexof(){
        String str="1,12,223,3,";
        String substring = str.substring(0, str.lastIndexOf(','));
        System.out.println(substring);

    }


    public static void main(String[] args) {
        URL resource = GenMultifunctionalApplicationTests.class.getResource("/");
        String path = resource.getPath();
            System.out.println(path);
    }
}
//d09601f953aacf5483083a4c0c5b427b
//https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=string&fenlei=256&oq=stringbuffer%25E7%2594%25A8%25E6%25B3%2595&rsv_pq=9f64d24b00017324&rsv_t=9a16troy6CEcThyvrPudkjOoeeSt2ZIO74%2BUIwslziBeyLugudoLpb82dE0&rqlang=cn&rsv_dl=tb&rsv_enter=1&rsv_btype=t&inputT=107949&sug=stringbuffer%25E5%2592%258Cstringbuilder%25E7%259A%2584%25E5%258C%25BA%25E5%2588%25AB&rsv_sug3=175&rsv_sug1=105&rsv_sug7=100&bs=stringbuffer%E7%94%A8%E6%B3%95
//ea28f000-19b6-48b8-878a-368810cc284e
//26ec9e33-6947-44ac-a969-3f92da15d883