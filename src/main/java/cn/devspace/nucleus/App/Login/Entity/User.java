/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/11 下午8:55
 */

package cn.devspace.nucleus.App.Login.Entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "login_user")
public class User extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private Long uid;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pwd", nullable = false)
    private String pwd;

    @CreationTimestamp
    @Column(name = "createTime")
    private Timestamp createTime;
}
