package com.promo;

import com.promo.dao.UserDOMapper;
import com.promo.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.promo"})
@RestController
@MapperScan("com.promo.dao")
public class PromoApplication {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String test() {

        UserDO userDO = userDOMapper.selectByPrimaryKey(1);

        if (userDO == null) {
            return "user not exists";
        } else {
            return    "<h2>"
                    + "姓名: "+userDO.getName()
                    +" 年纪: "+userDO.getAge()
                    +" 注册渠道: "+userDO.getRegisitMode()
                    +" 电话号码: "+userDO.getTelphone()
                    +" 性别: "+userDO.getGender()
                    + "</h2>";
        }
    }

    public static void main( String[] args ){
        SpringApplication.run(PromoApplication.class);
    }

}
