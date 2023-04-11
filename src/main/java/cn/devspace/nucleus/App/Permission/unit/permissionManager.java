/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2023/1/31 下午10:40
 */

package cn.devspace.nucleus.App.Permission.unit;

import cn.devspace.nucleus.App.Permission.entity.Permission;
import cn.devspace.nucleus.App.Permission.entity.impl.PermissionImpl;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.NucleusApplication;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

@Component
/**
 * 权限管理器
 * Permission manager
 */
public class permissionManager {

    /**
     * 权限组数据库操作
     * Permission group database operation
     */

    public DataBase dataBase;

    public static permissionManager permissionManager = new permissionManager();


    @PostConstruct
    public void setPermissionBaseMapper(){
        permissionManager.dataBase = new DataBase(this.getClass(), new Permission());
    }

    public Session getSession(){
        if (!dataBase.getSession().getTransaction().isActive()) {
            dataBase.getSession().beginTransaction();
        }
        // 清除缓存
        dataBase.getSession().clear();
        return dataBase.getSession();
    }

    /**
     * 创建一个新的权限组
     * Create a new permission group
     * @param permissionGroup 权限组 Permission group
     * @return 权限码字符串 Permission code string
     */
    public String newPermission(String[] permissionGroup){
        String permissionString = parsePermissionFormat(permissionGroup);
        Permission permission = new Permission();
        permission.setPermission(permissionString);
        String token = DigestUtils.md5DigestAsHex((System.currentTimeMillis()+permissionString).getBytes());
        permission.setToken(token);
        // TODO: 2023/1/31 保存到数据库 Save to database
        Session session = permissionManager.getSession();
        session.save(permission);
        session.getTransaction().commit();
        return token;
    }

    /**
     * 将权限组转换为权限码
     * Convert permission group to permission code
     * @param permissionGroup
     * @return
     */
    public String newPermission(List<String> permissionGroup){
        String[] result = new String[permissionGroup.size()+5];
        for (int i = 0; i < permissionGroup.size(); i++) {
            result[i] = permissionGroup.get(i);
        }
        return newPermission(result);
    }


    /**
     * 创建一个新的权限组
     * Create a new permission group
     * @param permissionGroup  权限组 Permission group
     * @return 权限码字符串 Permission code string
     */
    public String newPermission(String permissionGroup){
        return newPermission(PermissionString2Array(permissionGroup));
    }

    /**
     * 检查权限
     * Check permission
     * @param token 权限码 Permission code
     * @param permissionGroup 权限组 Permission group
     * @return 是否有权限 Has permission
     */
    public boolean checkPermission(String token, String permissionGroup){
        Session session = permissionManager.getSession();
        Criteria criteria = session.createCriteria(Permission.class);
        criteria.add(Restrictions.eq("token", token));
        Permission permission = (Permission) criteria.uniqueResult();
        if (permission == null){
            return false;
        }
        String permissionString = permission.getPermission();
        String[] permissionArray = PermissionString2Array(permissionString);
        for (String permissionItem:permissionArray){
            if (permissionItem.equals(permissionGroup)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取权限组
     * @param token 权限码 Permission code
     * @return 权限组 Permission group
     */
    public List<String> getPermissionList(String token) {
        try (Session session = permissionManager.getSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Permission> query = builder.createQuery(Permission.class);
            Root<Permission> root = query.from(Permission.class);
            query.where(builder.equal(root.get("token"), token));
            Permission permission = session.createQuery(query).uniqueResult();
            if (permission == null) {
                return null;
            }
            String permissionString = permission.getPermission();
            String[] array = PermissionString2Array(permissionString);
            return List.of(array);
        }
    }

    public boolean checkPermission(String token, String[] permissionGroup) {
        Session session = permissionManager.getSession();
        Criteria criteria = session.createCriteria(Permission.class);
        criteria.add(Restrictions.eq("token", token));
        Permission permission = (Permission) criteria.uniqueResult();
        if (permission == null) {
            return false;
        }
        String permissionString = permission.getPermission();
        String[] permissionArray = PermissionString2Array(permissionString);
        // 需要满足所有权限才能通过
        // Need to meet all permissions to pass
        for (String permissionItem : permissionGroup) {
            boolean hasPermission = false;
            for (String permissionItem2 : permissionArray) {
                if (permissionItem.equals(permissionItem2)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission){
                return false;
            }
        }
        return true;
    }



    /**
     * 生成权限组字符串
     * Generate permission group string
     * @param permissionGroup  权限组 Permission group
     * @return 权限组字符串 Permission group string
     */
    private String parsePermissionFormat(String[] permissionGroup){
        if (permissionGroup == null){
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i=0;i<permissionGroup.length;i++){
            if (i+1 == permissionGroup.length){
                result.append(permissionGroup[i]);
            }else {
                result.append(permissionGroup[i]).append(",");
            }
        }
        return result.toString();
    }

    /**
     * 将权限组字符串转换为数组
     * Convert permission group string to array
     * @param permissionGroup 权限组字符串 Permission group string
     * @return 权限组数组 Permission group array
     */
    private String[] PermissionString2Array(String permissionGroup){
        return permissionGroup.split(",");
    }

}
