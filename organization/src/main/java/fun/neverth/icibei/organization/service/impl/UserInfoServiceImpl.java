package fun.neverth.icibei.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.neverth.icibei.organization.dao.UserInfoMapper;
import fun.neverth.icibei.organization.entity.po.Role;
import fun.neverth.icibei.organization.entity.po.UserInfo;
import fun.neverth.icibei.organization.entity.po.UserRole;
import fun.neverth.icibei.organization.entity.vo.UserInfoVO;
import fun.neverth.icibei.organization.service.RoleService;
import fun.neverth.icibei.organization.service.UserInfoService;
import fun.neverth.icibei.organization.service.UserRoleService;
import fun.neverth.icibei.organization.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author neverTh
 * @since 2020-10-11
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserInfo getByUserId(String userId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public UserInfoVO getVoByUserId(String userId) {
        final Set<String> roleIds = userRoleService.queryByUserId(userId);
        Set<String> roles = new HashSet<>();
        for (String roleId : roleIds) {
            roles.add(roleService.get(roleId).getCode());
        }

        final UserInfo userInfo = getByUserId(userId);

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, userInfoVO);
        userInfoVO.setRoles(roles);
        return userInfoVO;
    }
}
