/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/8 上午10:07
 */

package cn.devspace.nucleus.App.Login.Entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import lombok.Data;

import javax.persistence.*;


@Data
@Table(name = "login_token")
@Entity
public class tokenEntity extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tid", nullable = false)
    private Long tid;

    @Column(name = "uid",nullable = false)
    private Long uid;
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "allowTime", nullable = false)
    private Long allowTime;

    @Column(name = "createTime", nullable = false)
    private Long createTime =System.currentTimeMillis();

    public void setAllowTime(int second){
        this.allowTime = System.currentTimeMillis()+second;
    }

}
