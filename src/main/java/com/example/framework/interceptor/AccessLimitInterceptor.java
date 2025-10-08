package com.example.framework.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.framework.annotation.AccessLimit;
import com.example.framework.service.RedisService;
import com.example.framework.utils.IpUtil;
import com.example.framework.utils.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.example.framework.constant.CommonConstant.APPLICATION_JSON;

/**
 * 该类用于实现访问频率限制的功能
 * @author: qinglong
 * @create-date: 2023/10/7 16:41
 */
@Log4j2
@Component
@SuppressWarnings("all")
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        //解决跨域问题
        //如果请求是跨域请求(请求方法是:OPTIONS),handler对象不是HandlerMethod
        //当发生跨域时,系统会先发送一条预请求,请求路径与真实请求一样,但是请求方式为OPTIONS
        //它不属于handler实例,如果我们不写下面这个判断,它会继续进行下面的操作,但是由于预请求没有
        //携带参数,在下面的判断中就会被直接拦截下来,这样导致这个请求失败
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                String key = IpUtil.getIpAddress(httpServletRequest) + "-" + handlerMethod.getMethod().getName();
                try {
                    long q = redisService.incrExpire(key, seconds);
                    if (q > maxCount) {
                        render(httpServletResponse, ResultVO.fail("请求过于频繁，" + seconds + "秒后再试"));
                        log.warn(key + "请求次数超过每" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                } catch (RedisConnectionFailureException e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
    private void render(HttpServletResponse response, ResultVO<?> resultVO) throws Exception {
        response.setContentType(APPLICATION_JSON);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(resultVO);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
