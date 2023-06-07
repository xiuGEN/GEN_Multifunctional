
package com.xiuGEN.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.xiuGEN.config.email.MergeEmailMessageInfo;
import com.xiuGEN.config.email.RegisterEmailMessageInfo;
import com.xiuGEN.config.email.ResetPasswordEmailMessageInfo;
import com.xiuGEN.context.PermissionContext;
import com.xiuGEN.context.SessionContext;
import com.xiuGEN.pojo.*;
import com.xiuGEN.service.FileInfoService;
import com.xiuGEN.service.UserService;
import com.xiuGEN.service.task.TaskSchedulerUrl;
import com.xiuGEN.utils.SendmailUtil;
import com.xiuGEN.utils.UUIDUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class UserController {

    @Value("${headphoto.position}")
    private String filepath;
    @Value("${file.position}")
    private String uploadFileLocation;
    @Value("${file.download}")
    private String downLocation;
    @Value("${share.downlocaltion}")
    private String sharedownlocaltion;
    @Autowired
    private UserService userService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private SendmailUtil sendmailUtil;
    @Autowired
    private TaskSchedulerUrl taskSchedulerUrl;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public String login(String username, String password, String rememberMe, HttpServletRequest request) {
        /*
         * 用户登录
         */
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(Boolean.parseBoolean(rememberMe));
        JSONObject json = new JSONObject();
        try {
            subject.login(token); //进行密码的判断，密码错误则抛出异常
            try {
                subject.checkRole("user");
            }catch (AuthorizationException authorizationException){
                logger.error("用户无角色登入" + authorizationException.getMessage());
                json.put("login_status", "invalid");
                json.put("login_msg", "用户无角色登入--请联系管理员");
                return json.toString();
            }
            json.put("login_status", "success");
            json.put("redirect_url", "/index");
            return json.toString();
        } catch (Exception e) {
            logger.error("用户登录失败" + e.getMessage());
            json.put("login_status", "invalid");
            json.put("login_msg", "用户名密码错误--请重新输入");
            return json.toString();
        }
    }

    /*
     * 用户退出登录
     */
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
            Cookie cookie = new Cookie("rememberMe", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        response.sendRedirect("/login");
    }

    /*
     * 用户注册
     */
    @PostMapping("/register")
    public String register(User user, HttpServletRequest request) {
        // 个人感觉有点小争议,该可能要改动很多,暂时封存
//        int code = userService.addUser(user);

        JSONObject jsondata = new JSONObject();
        if (user != null) {
            SessionContext sessionContext = SessionContext.getInstance();
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            sessionContext.addSession(session);
            UUID uuid = UUID.randomUUID();
            String replace = uuid.toString().replace("-", "");
            String replace2 = uuid.toString().replace("-", "");
            String sid = session.getId();
            RegisterEmailMessageInfo emailMessageInfo = new RegisterEmailMessageInfo(request.getRequestURL().toString(),
                    (replace + sid + replace2));
            try {
                sendmailUtil.setSendTo(user.getUemail());
                sendmailUtil.sendText(emailMessageInfo.getTitle(), emailMessageInfo.getText(), true, null);
                jsondata.put("register_status", "success");
            } catch (Exception e) {
                logger.error("用户注册失败" + e.getMessage());
                jsondata.put("register_status", "invalid");
                e.printStackTrace();
            }
        } else {
            jsondata.put("register_status", "invalid");
        }
        return jsondata.toString();
    }

    /*
     * 是否被注册
     */
    @PostMapping("/exist/{check}")
    public String isexist(@PathVariable(name = "check") String checkName, String checkValue) {
        JSONObject exist = new JSONObject();
        User user = userService.existCheck(checkName, checkValue);
        if (user != null) {
            exist.put("exists", "exists");
        } else {
            exist.put("exists", "noexists");
        }
        return exist.toString();
    }

    @PostMapping("/exist2/{check}")
    public String isexist2(@PathVariable(name = "check") String checkName, String checkValue) {
        JSONObject exist = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        User user = userService.existCheck(checkName, checkValue);
        if (user != null && currentUser != null) {
            if (!user.getUid().equals(currentUser.getUid())) {
                exist.put("exists", "exists");
            } else
                exist.put("exists", "noexists");

        } else {
            exist.put("exists", "noexists");
        }
        return exist.toString();
    }

    /*
     * 对忘记密码的用户发送邮件
     *
     */

    @PostMapping("/forgot")
    public String fotgot(HttpServletRequest request, String email) {
        JSONObject jsonObject = new JSONObject();
        User user = userService.checkUserByEmail(email);
        if (user == null) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(60 * 30);
        String sessionId = session.getId();
        SessionContext context = SessionContext.getInstance();
        context.addSession(session);
        StringBuffer buffer = request.getRequestURL();
        StringBuffer href = buffer.delete(buffer.lastIndexOf("/"), buffer.length());
        ResetPasswordEmailMessageInfo info = new ResetPasswordEmailMessageInfo(href + "/resetpassword",
                UUIDUtil.getRandomNuber() + sessionId + UUIDUtil.getRandomNuber());
        sendmailUtil.setSendTo(email);
        try {
            sendmailUtil.sendText(info.getTitle(), info.getText(), true, null);
        } catch (Exception e) {
            logger.error("用户重置密码" + e.getMessage());
            jsonObject.put("register_status", "invalid");
            e.printStackTrace();
            return jsonObject.toString();
        }
        jsonObject.put("register_status", "success");
        return jsonObject.toString();
    }

    /*
     * 对用户进行重置密码
     *
     *
     *
     */
    @PostMapping("/resetpassword")
    public String resetpassword(HttpServletRequest request, String password) {
        JSONObject jsonObject = new JSONObject();
        if (password == null || password.equals("")) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        String sessionId = request.getSession().getId();
        SessionContext sessionContext = SessionContext.getInstance();
        HttpSession session = sessionContext.getSession(sessionId);
        if (session == null) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        user.setUpassword(DigestUtil.md5Hex(password));
        int code = userService.changeUserInfo(user);
        if (code > 0) {
            jsonObject.put("register_status", "success");
        } else {
            jsonObject.put("register_status", "invalid");
        }
        sessionContext.delSession(session);
        return jsonObject.toString();
    }

    /*
     * 对用户进行重置密码
     *
     *
     *
     */
    @PostMapping("/resetpasswordByUid")
    public String resetpasswordById(String uid, String password) {
        JSONObject jsonObject = new JSONObject();
        if (password == null || password.equals("")) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        if (uid == null) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        User user = userService.existCheck("uid", uid);
        if (user == null) {
            jsonObject.put("register_status", "invalid");
            return jsonObject.toString();
        }
        user.setUpassword(DigestUtil.md5Hex(password));
        int code = userService.changeUserInfo(user);
        if (code > 0) {
            jsonObject.put("register_status", "success");
        } else {
            jsonObject.put("register_status", "invalid");
        }
        return jsonObject.toString();
    }

    /*
     *
     * save保存
     *
     *
     */
    @PostMapping("/save")
    public String saveUserInfo(User user) {
        JSONObject json = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User current = (User) subject.getPrincipal();
        if (current != null) {
            user.setUid(current.getUid());
        }
        if (current.getOldpassword() != null && current.getUpassword().equals(user.getUpassword())) {
            user.setUpassword(current.getOldpassword());
            current.setUpassword(current.getOldpassword());
        } else {
            user.setUpassword(DigestUtil.md5Hex(user.getUpassword()));
        }
        user.setUpicture(current.getUpicture());//头像修改更新不用修改
        int code = userService.changeUserInfo(user);
        current.setOldpassword(null);
        /*
         * 将当前用户修改
         * */
        String realname = subject.getPrincipals().getRealmNames().iterator().next();
        SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection(user, realname);
        subject.runAs(simplePrincipalCollection);

        if (code > 0) {
            json.put("register_status", "success");
        } else {
            json.put("register_status", "invalid");
        }
        return json.toString();
    }

    /*
     *
     * admin管理员save保存
     *
     *
     */
    @PostMapping("/saveByAdmin")
    public String saveUserInfoByAdmin(UserDto userDto) {
        int code = 0;
        int codecapacity = 0;
        JSONObject json = new JSONObject();
        if (userDto != null) {
            User user = userService.existCheck("uid", String.valueOf(userDto.getUid()));
            if (user != null) {
                user.setUemail(userDto.getUemail());
                Capacity capacity = userService.getCapacity(user.getUid());
                Double size = userDto.getSize();
                double v = size * (1024.0 * 1024.0 * 1024.0);
                if (capacity == null) {
                    capacity = new Capacity();
                    capacity.setOwnerId(user.getUid());
                    Double aDouble = Double.valueOf(v);
                    capacity.setCapacitySize(String.valueOf(aDouble.intValue()));
                    codecapacity = userService.saveCapacity(capacity);
                } else {
                    Double aDouble = Double.valueOf(v);
                    capacity.setCapacitySize(String.valueOf(aDouble.intValue()));
                    codecapacity = userService.changeCapacity(capacity);
                }
                code = userService.changeUserInfo(user);
            }
        }
        if (code > 0 && codecapacity > 0) {
            json.put("register_status", "success");
        } else {
            json.put("register_status", "invalid");
        }
        return json.toString();
    }


    /*
     *
     * 对当前用户信息的获取,并显示在页面中
     *
     *
     */
    @GetMapping("/getUserInfo")
    public String getUserInfo() {
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        if (currentUser.getOldpassword() == null || currentUser.equals("")) {
            currentUser.setOldpassword(currentUser.getUpassword());
            currentUser.setUpassword(RandomUtil.randomString(8));
        }
        jsonObject.put("user", currentUser);

        return jsonObject.toString();
    }

    /*
     *
     * 上传头像图片处理
     *
     */
    @PostMapping("/image/headphoto")
    public String changeHeadphoto(MultipartFile imageFile, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        String imagename = System.currentTimeMillis() + UUIDUtil.getRandomNuber() + currentUser.getUid()
                + UUIDUtil.getFileType(imageFile.getOriginalFilename());
        try {
            //解决读取配置文件中的中文，造成乱码，从而导致找不到路径
            if (UUIDUtil.isMessyCode(filepath))
                filepath = new String(filepath.getBytes(StandardCharsets.ISO_8859_1),"UTF-8");
            imageFile.transferTo(new File(filepath, imagename));
            if (currentUser.getUpicture() != null && currentUser.getUpicture() != "") {
                String oldimage = currentUser.getUpicture();
                File file = new File(filepath, oldimage.substring(oldimage.lastIndexOf("/") + 1));
                file.delete();
            }
            String temppassword = null;
            if (currentUser.getOldpassword() != null) {
                temppassword = currentUser.getUpassword();
                currentUser.setUpassword(currentUser.getOldpassword());
            }
            currentUser.setUpicture("static/headphone/" + imagename);
            userService.changeUserInfo(currentUser);
            if (temppassword != null && !temppassword.equals(""))
                currentUser.setUpassword(temppassword);
            /*
             * 将当前用户修改
             * */
            String realname = subject.getPrincipals().getRealmNames().iterator().next();
            SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection(currentUser, realname);
            subject.runAs(simplePrincipalCollection);


            jsonObject.put("status", "success");
            jsonObject.put("upicture", currentUser.getUpicture());
        } catch (Exception e) {
            logger.error("用户上传头像失败" + e.getMessage());
            e.printStackTrace();
            jsonObject.put("status", "invalid");
        }
        return jsonObject.toString();
    }

    /*
     * 处理上传的文件
     **/
    @PostMapping("/upload")
    public String uploadfile(MultipartFile[] files, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        int allFileSize = 0;
        if (files != null && files.length > 0) {
            Subject subject = SecurityUtils.getSubject();
            User current = (User) subject.getPrincipal();
            if(UUIDUtil.isMessyCode(uploadFileLocation)){
                uploadFileLocation = new String(uploadFileLocation.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
            }
            File userFilePath = new File(uploadFileLocation + current.getUid());
            Capacity capacity = userService.getCapacity(current.getUid());
            if (capacity == null) {
                json.put("status", "invalid");
                json.put("message", "您无储存文件的权限,请向小根申请");
                return json.toString();
            }
            long userSize = Long.valueOf(capacity.getCapacitySize());
            if (!userFilePath.exists()) {
                userFilePath.mkdirs();
            }
            for (MultipartFile file : files) {
                FileInfo fileInfo = new FileInfo();
                allFileSize += file.getSize();
                try {
                    if (userSize < allFileSize) {
                        json.put("status", "invalid");
                        json.put("message", "容量已满");
                        return json.toString();
                    }
                    File isexistFile = new File(userFilePath, file.getOriginalFilename());
                    if (!isexistFile.exists()) {
                        file.transferTo(isexistFile);
                        fileInfo.setFileName(file.getOriginalFilename());
                        fileInfo.setSize(String.valueOf(file.getSize()));
                        fileInfo.setOwnerId(current.getUid());
                        fileInfo.setFileLocation("uploadFile/" + current.getUid());
                        fileInfo.setSuffix(UUIDUtil.getFileType(file.getOriginalFilename()));
                        fileInfoService.addFileInfo(fileInfo);
                    }
                } catch (IllegalStateException | IOException e) {
                    logger.error("用户上传文件" + e.getMessage());
                    json.put("status", "invalid");
                    json.put("message", "文件上传失败,请检查文件名");
                    return json.toString();
                }
            }
            json.put("status", "success");
            json.put("message", "上传成功");
        } else {
            json.put("status", "invalid");
            json.put("message", "文件上传失败");
        }
        return json.toString();
    }

    /*
     * 显示分页数据
     *
     */
    @PostMapping("/filesInfo")
    public String pageInfo(String page) {
        Integer pageSize = 12;
        Integer pagenum = 1;
        if (page != null && !page.equals("")) {
            pagenum = Integer.valueOf(page);
        }
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        PageInfo<FileInfo> pageInfo = fileInfoService.getPageFiles(currentUser, pagenum, pageSize);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("pageInfo", pageInfo);
        jsonObject.put("sharedownlocaltion", sharedownlocaltion);
        return jsonObject.toString();
    }

    /*
     * 显示相关信息
     *
     */
    @PostMapping("/filesInfo2/{typename}")
    public String pageInfo2(@PathVariable(name = "typename") String typename) {
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        JSONObject jsonObject = new JSONObject();
        if (typename != null) {
            if (typename.equals("capacity")) {
                Capacity capacity = userService.getCapacity(currentUser.getUid());
                jsonObject.put("capacity", capacity);
            } else if (typename.equals("other")) {
                List<FileInfo> fileInfoList = fileInfoService.getFileType(currentUser.getUid());
                for (FileInfo file : fileInfoList) {
                    file.setTypeName("file");
                }
                jsonObject.put("files", fileInfoList);
            } else {
                List<FileInfo> fileInfoList = fileInfoService.getFileType(currentUser.getUid(), typename);
                jsonObject.put("files", fileInfoList);
            }
        }
        return jsonObject.toString();
    }

    @PostMapping("/filesInfoType")
    public String fileInfoType(String typename, String page) {
        Integer pageSize = 12;
        Integer pagenum = 1;
        if (page != null && !page.equals("")) {
            pagenum = Integer.parseInt(page);
        }
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        JSONObject jsonObject = new JSONObject();
        if (typename != null) {
            if (typename.equals("all")) {
                PageInfo<FileInfo> pageInfo = fileInfoService.getPageFiles(currentUser, pagenum, pageSize);
                jsonObject.put("pageInfo", pageInfo);
            } else {
                /*
                 * 注意顺序,要先设定pageHelper,才开始进行查询相关操作,不然分页无效,有空看看源码追究
                 */
                PageHelper.startPage(pagenum, pageSize);
                List<FileInfo> fileInfoList = fileInfoService.getFileType(currentUser.getUid(), typename);
                PageInfo<FileInfo> pageInfo = new PageInfo<>(fileInfoList);
                jsonObject.put("pageInfo", pageInfo);
            }
        }
        return jsonObject.toString();
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{path}")
    public void download(@PathVariable String path, HttpServletResponse response) {
        if (path != null && !path.equals("")) {
            try {

                byte[] decodedBytes = Base64.getDecoder().decode(path);
                path = new String(decodedBytes);
               path = URLDecoder.decode(path, "UTF-8");
                File file = new File(downLocation + path);
                String filename = file.getName();
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStream fis = new BufferedInputStream(fileInputStream);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                response.reset();
                // 设置response的Header
                response.setCharacterEncoding("UTF-8");
                // Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
                // attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline;
                // filename=文件名.mp3"
                // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
                response.addHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                // 告知浏览器文件的大小
                response.addHeader("Content-Length", "" + file.length());
                OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                outputStream.write(buffer);
                outputStream.flush();
            } catch (Exception ex) {
                logger.error("用户下载文件失败:" + ex.getMessage());
                ex.printStackTrace();
            }

        }

    }

    /**
     * @author: GEN
     * @date: 2023/5/28
     * @Param: null
     * @return 对分享的路径进行控制,开启一个定时任务进行检测5分钟有效
     */
    @GetMapping("/share/urlsetvalid")
    @ResponseBody
    public String donwloadUrlSetValid(String urlpath){
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isNullOrEmpty(urlpath)){
            PermissionContext.setPermissionPath(urlpath,"anon");
            ThreadPoolTaskScheduler taskScheduler = taskSchedulerUrl.getTaskScheduler();
            if(taskScheduler==null) taskSchedulerUrl.startScheduledTasks();
            jsonObject.put("code",200);
            jsonObject.put("msg","口令已复制，请在"+PermissionContext.PATH_EXPIRATION_SECONDS/60+"分钟之内使用,否则该链接失效");
        }else{
            jsonObject.put("code",500);
            jsonObject.put("msg","分享失败");
        }
        return jsonObject.toString();

    }
    /*
     * 文件删除
     *
     */
    @PostMapping("/delete")
    public String delete(String path) {
        JSONObject json = new JSONObject();
        if (path != null) {
            Subject subject = SecurityUtils.getSubject();
            User currentUser = (User) subject.getPrincipal();
            if(UUIDUtil.isMessyCode(downLocation)){
                downLocation = new String(downLocation.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
            }
            File delfile = new File(downLocation + path);
            if (delfile.exists() && currentUser != null) {
                try {
                    int i = fileInfoService.delFileByFileName(currentUser.getUid(), delfile.getName());
                    boolean delete = delfile.delete();
                    if (i > 0 && delete) {
                        json.put("states", "success");
                    } else {
                        json.put("states", "invalid");
                    }
                } catch (Exception e) {
                    logger.error("用户删除失败" + e.getMessage());
                    json.put("states", "invalid");
                    return json.toString();
                }

            } else {
                json.put("states", "invalid");
            }
        }
        return json.toString();
    }

    /*
     * 批量删除
     *
     * */
    @PostMapping("/batchdelete")
    public String batchDelete(String path) {
        JSONObject json = new JSONObject();
        int count = 0;
        int success = 0;
        int error = 0;
        if (path != null) {
            Subject subject = SecurityUtils.getSubject();
            User currentUser = (User) subject.getPrincipal();
            String[] paths = path.split("--->");
            count = paths.length;
            for (int j = 0; j < paths.length; j++) {
                path = paths[j];
                if(UUIDUtil.isMessyCode(downLocation)){
                    downLocation = new String(downLocation.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
                }
                File delfile = new File(downLocation + path);
                if (delfile.exists() && currentUser != null) {
                    try {
                        int i = fileInfoService.delFileByFileName(currentUser.getUid(), delfile.getName());
                        boolean delete = delfile.delete();
                        if (i > 0 && delete) {
                            success++;
                        } else {
                            error++;
                        }
                    } catch (Exception e) {
                        logger.error("用户删除失败" + e.getMessage() + "文件:" + path);
                        error++;
                    }
                } else {
                    error++;
                }
            }
        }
        if (success == count) {
            json.put("states", "success");
        } else {
            json.put("states", "invalid");
        }
        json.put("msg", "成功:" + success + "  " + "失败:" + error);
        return json.toString();
    }

    /*
     *
     * 搜索:按类型和名字搜索文件
     * */
    @PostMapping("/searchFile")
    public String searchFile(String typename, String filename) {
        Integer pageSize = 12;
        Integer pagenum = 1;
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();
        JSONObject jsonObject = new JSONObject();
        if (typename != null) {
            List<FileInfo> filesByName = null;
            if (typename.equals("all")) {
                filesByName = fileInfoService.searchFilesByName(currentUser.getUid(), filename);
            } else {
                filesByName = fileInfoService.searchFilesByName(currentUser.getUid(), typename, filename);
            }
            PageHelper.startPage(pagenum, pageSize);
            PageInfo<FileInfo> pageInfo = new PageInfo<>(filesByName);
            jsonObject.put("pageInfo", pageInfo);
        }
        return jsonObject.toString();
    }


    @GetMapping("/test")
    public void test(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(10);
        if (session.isNew()) {
            System.out.println("Newsession:" + session.getId());
        } else {
            System.out.println("Oldsession:" + session.getId());
        }
        for (Cookie c : cookies) {
            System.out.println(c.getName() + " :" + c.getValue() + ":" + c.getMaxAge());
            System.out.println(request.getRequestURL());
        }
    }

    /*
     * 测试文件上传进度
     *
     *
     *
     * @RequestMapping("/fileUpload")
     *
     * @ResponseBody public String fileUpload2(@RequestParam("file")
     * CommonsMultipartFile file) throws IOException { JSONObject member = new
     * JSONObject(); try{ String
     * path="C:\\Users\\Administrator\\Desktop\\upload\\"+new
     * Date().getTime()+file.getOriginalFilename(); File newFile=new File(path);
     * //通过CommonsMultipartFile的方法直接写文件（注意这个时候） file.transferTo(newFile);
     * //构建series所需参数 member.put("success", "ok"); //对应series.name }catch (Exception
     * e){ member.put("success", "error"); //对应series.name } return
     * member.toString(); }
     *
     */

    /*
     *   获取用户和权限列表
     * */
    @GetMapping("/getUsersAndRoles")
    public String getUserAndRole() {
        List<User> allUsers = userService.getAllUsers();
        List<Role> roleByIds = userService.getAllRoles();
        Map<String, List> userMap = new HashMap<>();
        for (User user : allUsers) {
            changeUserInfo(user);
        }
        userMap.put("user", allUsers);
        userMap.put("role", roleByIds);
        return JSONUtil.toJsonStr(userMap);
    }

    @PostMapping("/userAuthorization")
    public String userauthorization(String userids, String roles) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "0");
        resultMap.put("msg", "授权成功");
        if (!StringUtils.isNullOrEmpty(userids) && !StringUtils.isNullOrEmpty(roles)) {
            String[] useridArray = userids.split(",");
            String[] roleArray = roles.split(",");
            for (String s : useridArray) {
                List<Role> roleById = userService.getRoleById(Integer.valueOf(s));
                for (String noRole : roleArray) {
                    boolean flag = true;
                    for (Role role : roleById) {
                        if (noRole.equals(String.valueOf(role.getRoleid()))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        try {
                            int i = userService.saveRole(new Roleuser(Integer.valueOf(noRole), Integer.valueOf(s)));
                        } catch (Exception e) {
                            resultMap.put("code", "1");
                            resultMap.put("msg", "授权失败");
                            return JSONUtil.toJsonStr(resultMap);
                        }
                    }
                }
            }
        }
        return JSONUtil.toJsonStr(resultMap);
    }


    @GetMapping("/getRoleById")
    public String getRoleById(String userid) {
        Map<String, List> userMap = new HashMap<>();
        if (!StringUtils.isNullOrEmpty(userid)) {
            List<Role> allRoles = userService.getAllRoles();
            List<Role> roleById = userService.getRoleById(Integer.valueOf(userid));
            for (Role role : allRoles) {
                for (Role selfRole : roleById) {
                    if (role.getRoleid() == selfRole.getRoleid()) {
                        role.setFlag(true);
                    }
                }
            }
            userMap.put("role", allRoles);
        }
        return JSONUtil.toJsonStr(userMap);
    }


    @PostMapping("/userAuthorizationById")
    public String userauthorizationById(String userid, String roles) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "0");
        resultMap.put("msg", "授权成功");
        if (!StringUtils.isNullOrEmpty(userid)) {
            try {
                userService.delRolesById(Integer.valueOf(userid));
                if (!StringUtils.isNullOrEmpty(roles)) {
                    String[] roleArray = roles.split(",");
                    for (String role : roleArray) {
                        userService.saveRole(new Roleuser(Integer.valueOf(role), Integer.valueOf(userid)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                resultMap.put("code", "1");
                resultMap.put("msg", "保存失败");
                return JSONUtil.toJsonStr(resultMap);
            }
        }
        return JSONUtil.toJsonStr(resultMap);
    }

    @PostMapping("/addUserByAdmin")
    public String addUserByAdmin(String jsonParam) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "1");
        resultMap.put("msg", "注册失败");
        if (!StringUtils.isNullOrEmpty(jsonParam)) {
            User user = JSONUtil.toBean(jsonParam, User.class);
            String uname = user.getUname();
            if (!StringUtils.isNullOrEmpty(uname)) {
                User existUser = userService.existCheck("uname", uname);
                if (existUser == null) {
                    try {
                        user.setUpassword(DigestUtil.md5Hex(user.getUpassword()));
                        user.setUpicture("static/headphone/4dd36dcbbc7b4d828525a2ae1920d5761.png");
                        user.setUbirthdate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        if(StringUtils.isNullOrEmpty(user.getUemail())){
                            String  uemail= RandomUtil.randomNumbers(8);
                            user.setUemail(uemail+"@qq.com");
                        }
                        if(StringUtils.isNullOrEmpty(user.getUphone())){
                            String uphone = RandomUtil.randomNumbers(11);
                            user.setUphone(uphone);
                        }
                        if(StringUtils.isNullOrEmpty(user.getUrealname())){
                            String urealname = RandomUtil.randomString(4);
                            user.setUrealname(urealname);
                        }
                        userService.addUser(user);
                        User newUser = userService.existCheck("uname", user.getUname());
                        if (newUser == null) {
                            return JSONUtil.toJsonStr(resultMap);
                        }
                        /*用户授权*/
                        Roleuser roleuser = new Roleuser();
                        /*1.是admin   2是 user*/
                        roleuser.setRoleid(2);
                        roleuser.setUserid(newUser.getUid());
                        int i = userService.saveRole(roleuser);
                        resultMap.put("code", "0");
                        resultMap.put("msg", "注册成功");
                        return JSONUtil.toJsonStr(resultMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                        resultMap.put("code", "1");
                        resultMap.put("msg", "注册失败");
                    }
                }

            }

        }
        return JSONUtil.toJsonStr(resultMap);
    }

    private void changeUserInfo(User user) {
        if (user != null) {
            user.setUpassword("");
            user.setUpicture("");
            user.setOldpassword("");
            user.setUemail("");
            user.setUaddress("");
            user.setUphone("");
            user.setUrealname("");
            user.setUbirthdate("");
        }
    }

    /**
     * @author: GEN
     * @date: 2023/5/24
     * @Param: 邮箱号
     * @return  用于账号合并，并返回标识符
     */
    @PostMapping("/sendVerificationCode")
        public String sendMailVerificationCode(HttpServletRequest request,String mail){
        JSONObject jsonObject = new JSONObject();
        if(mail!= null || !mail.equals("") ){
            User user = userService.checkUserByEmail(mail);
            if (user == null) {
                jsonObject.put("code", 500);
                jsonObject.put("msg", "未找到该用户");
                return jsonObject.toString();
            }
            HttpSession session = request.getSession();
            String code = UUIDUtil.randomNum(5);
            SessionContext sessionContext = SessionContext.getInstance();
            sessionContext.addSession(session);
            session.setAttribute("verificationCode", code);
            session.setAttribute("user",user);
            MergeEmailMessageInfo info = new MergeEmailMessageInfo("5",code);
            sendmailUtil.setSendTo(mail);
            try {
                sendmailUtil.sendText(info.getTitle(), info.getText(), true, null);
            } catch (Exception e) {
                logger.error("合并邮箱发送验证码失败" + e.getMessage());
                jsonObject.put("code", 500);
                jsonObject.put("msg", "验证码发送失败");
                return jsonObject.toString();
            }
            jsonObject.put("code", 200);
            jsonObject.put("msg", "发送成功");
        }else {
            jsonObject.put("code", 500);
            jsonObject.put("msg", "邮箱号有误");
        }
        return jsonObject.toString();
    }
    
    /**
     * @author: GEN
     * @date: 2023/5/24
     * @Param: null
     * @return  验证验证码
     */
    @RequestMapping("/verificationCode")
    public String verificationVerificationCode(HttpServletRequest request ,String code){
        JSONObject jsonObject  = new JSONObject();
        if(org.springframework.util.StringUtils.hasLength(code)){
            HttpSession session = request.getSession();
            String verificationCode = session.getAttribute("verificationCode").toString();
            if(code.equals(verificationCode)){
                jsonObject.put("code",200);
                jsonObject.put("url","/alipay/login");
                jsonObject.put("msg","验证码正确");
            }
        }else{
            jsonObject.put("msg","验证码为空");
            jsonObject.put("code",500);
        }
        return jsonObject.toString();
    }

    @RequestMapping("/loginUrl")
    public String sendloginUrl(String sign){
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isNullOrEmpty(sign)){
            jsonObject.put("code",200);
            switch (sign){
                case "1" :{
                    jsonObject.put("msg","/alipay/login");
                    break;
                }
                default: jsonObject.put("msg","/login");
            }
            return jsonObject.toString();
        }
        jsonObject.put("code","500");
        jsonObject.put("msg","请求出错");
        return jsonObject.toString();
    }
    public static void main(String[] args) {
        String a = "12,23,2,";
        System.out.println(a.substring(0, a.length() - 1));
    }

}
