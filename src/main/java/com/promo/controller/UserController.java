package com.promo.controller;

import com.alibaba.druid.util.StringUtils;
import com.promo.controller.viewObject.UserVO;
import com.promo.error.BusinessException;
import com.promo.error.EmBusinessError;
import com.promo.response.CommonReturnType;
import com.promo.service.UserService;
import com.promo.service.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")    // 跨域处理
public class UserController extends BaseController{

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户注册-接口
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") String gender,
                                     @RequestParam(name = "age") String age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //从前端录入的字段
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);

        // 验证推送出去的验证码和前端录入的字段是否相同
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    /**
     * 用户注册-短信验证码服务
     */
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){

        //验证码生成方式
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // httpsession 绑定 手机号码 与 验证码
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        //将 OTP 验证码通过短信通道发送给用户, 该段逻辑可以嵌入阿里云的短信通知方式
        logger.info("--------- telphone: " + telphone );
        logger.info("--------- otpCode : " + otpCode);

        return CommonReturnType.create(null);

    }

    /**
     * 用户服务 - 用户登录
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 必要的参数校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserModel userModel = userService.validateLogin(telphone,this.EncodeByMd5(password));

        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");

        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);

        return CommonReturnType.create(uuidToken);

    }
    /**
     * @param id
     * @throws BusinessException
     */
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {

        //调用 service 服务获取对应 id 的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供 UI 使用的 viewobject
        UserVO userVO = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }



    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    private UserVO convertFromModel(UserModel userModel){

        if(userModel == null){
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);

        return userVO;
    }

}
