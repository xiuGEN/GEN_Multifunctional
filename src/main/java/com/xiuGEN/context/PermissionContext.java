package com.xiuGEN.context;

import com.xiuGEN.utils.BeanProviderUtil;
import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.beans.beancontext.BeanContextServiceProvider;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: GEN_Multifunctional
 * @description: 进行权限的统一动态管理
 * @author: xiuGEN
 * @create: 2023-05-28 11:22
 *
 **/
@Component
public class PermissionContext {
    private static Logger logger = LoggerFactory.getLogger(PermissionContext.class);
    private static ShiroFilterFactoryBean shiroFilterFactoryBean;
    public static final long PATH_EXPIRATION_SECONDS = 300;//五分钟
    private static Map<String, Path> tempPaths = new ConcurrentHashMap<>();

    @Autowired
    private void setShiroFilter(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        PermissionContext.shiroFilterFactoryBean = shiroFilterFactoryBean;
    }

    public static Map<String, String> getFilterMap() {
        return shiroFilterFactoryBean.getFilterChainDefinitionMap();
    }

    public static void setFilterMap(Map filterMap) {
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
    }

    public static Map<String, Path> getTempPaths() {
        return PermissionContext.tempPaths;
    }

    /**
     * @return
     * @author: GEN
     * @date: 2023/5/28
     * @Param: null
     * @Description: 将动态的添加权限访问路径，时间为5分钟
     */
    public static void setPermissionPath(String path, String role) {
        Map<String, String> filterMap = getFilterMap();
        if (StringUtils.hasLength(path) && StringUtils.hasLength(role)) {
            Path pathobj = new Path(path, LocalDateTime.now().plusSeconds(PATH_EXPIRATION_SECONDS));
            if (!tempPaths.containsKey(path)) {
                tempPaths.put(path, pathobj);
            } else if (isPathExpired(path)) {
                Path pathExist = tempPaths.get(path);
                pathExist.setExpirationTime(LocalDateTime.now().plusSeconds(PATH_EXPIRATION_SECONDS));
                tempPaths.put(path, pathExist);
            }
            filterMap.put(path, role);
            disposePath(filterMap,"/**");
            updateFilterChainDefinitionMap(filterMap);
        }
    }

    public static void setPermissionPaths(Map<String, String> paths) {
        Map<String, String> newfilterMap =getFilterMap();
        if (paths != null && !paths.isEmpty()) {
            for (Map.Entry<String, String> entry : paths.entrySet()) {
                Path pathobj = new Path(entry.getKey(), LocalDateTime.now().plusSeconds(PATH_EXPIRATION_SECONDS));
                if (!tempPaths.containsKey(entry.getKey())) {
                    tempPaths.put(entry.getKey(), pathobj);
                } else if (isPathExpired(entry.getKey())) {
                    Path pathExist = tempPaths.get(entry.getKey());
                    pathExist.setExpirationTime(LocalDateTime.now().plusSeconds(PATH_EXPIRATION_SECONDS));
                    tempPaths.put(entry.getKey(), pathExist);
                }
                newfilterMap.put(entry.getKey(), entry.getValue());
            }
            disposePath(newfilterMap,"/**");
            updateFilterChainDefinitionMap(newfilterMap);
        }

    }

    public static void removePermissionPaths(String path) {
        if (StringUtils.hasLength(path) && tempPaths.containsKey(path)){
            tempPaths.remove(path);
            Map<String, String> filterMap = getFilterMap();
            filterMap.remove(path);
            updateFilterChainDefinitionMap(filterMap);
        }
    }



    public static boolean isPathExpired(String path) {
        LocalDateTime expirationTime = tempPaths.get(path).getExpirationTime();
        return expirationTime != null && LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * @author: XIUGEN
     * @date: 2023/6/2
     * @Param: null
     * @return  更新权限
     */
    public static void updateFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
        try {
            /*HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            ServletContext servletContext = request.getSession().getServletContext();
            AbstractShiroFilter shiroFilter = (AbstractShiroFilter) WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getBean("shiroFilterFactoryBean");
*/
            AbstractShiroFilter shiroFilter =(AbstractShiroFilter) BeanProviderUtil.getBean(AbstractShiroFilter.class);
            synchronized (shiroFilter) {
                // 获取过滤管理器
                PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
                DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
                // 清空初始权限配置
                manager.getFilterChains().clear();
                // 重新获取资源
                Map<String, String> chains = filterChainDefinitionMap;
                for (Map.Entry<String, String> entry : chains.entrySet()) {
                    String url = entry.getKey();
                    String chainDefinition = entry.getValue().trim().replace(" ", "");
                    manager.createChain(url, chainDefinition);
                }
                //Set<Map.Entry<String, String>> entries = chains.entrySet();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * @author: XIUGEN
     * @date: 2023/6/2
     * @Param: null
     * @return 将/** 路径放到最前面
     */
    private static void disposePath(Map<String,String> paths,String path){
        if(paths != null && !paths.isEmpty()){
            String removeValue = null;
            for(Map.Entry<String,String> entry:paths.entrySet()){
               if (entry.getKey().equals(path)){
                   removeValue = entry.getValue();
                    paths.remove(path);
                   break;
               }
           }
            if(removeValue != null){
                paths.put(path,removeValue);
            }
        }

    }
}
