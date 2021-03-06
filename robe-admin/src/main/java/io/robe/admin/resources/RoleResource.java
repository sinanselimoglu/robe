package io.robe.admin.resources;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import io.robe.admin.hibernate.dao.RoleDao;
import io.robe.admin.hibernate.entity.Role;
import io.robe.auth.Credentials;
import io.robe.common.exception.RobeRuntimeException;
import org.hibernate.Hibernate;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


@Path("role")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {


    @Inject
    RoleDao roleDao;

    @Path("all")
    @GET
    @UnitOfWork
    public List<Role> getRoles(@Auth Credentials credentials) {

        return roleDao.findAll(Role.class);
    }

    @GET
    @UnitOfWork
    @Path("{userId}")
    public Role get(@Auth Credentials credentials, @PathParam("userId") String id) {
        Role role = roleDao.findById(id);
        Hibernate.initialize(role.getRoles());
        return role;
    }

    @PUT
    @UnitOfWork
    public Role create(@Auth Credentials credentials, @Valid Role role) {
        Optional<Role> checkUser = roleDao.findByName(role.getCode());
        if (checkUser.isPresent())
            throw new RobeRuntimeException("Code", role.getCode() + " already used by another role. Please use different code.");
        role = roleDao.create(role);
        return role;
    }

    @POST
    @UnitOfWork
    public Role update(@Auth Credentials credentials, Role role) {
        roleDao.detach(role);
        Role entity = roleDao.findById(role.getOid());
        entity.setName(role.getName());
        entity.setCode(role.getCode());
        entity = roleDao.update(entity);
        return entity;

    }


    @DELETE
    @UnitOfWork
    public Role delete(@Auth Credentials credentials, Role role) {
        role = roleDao.findById(role.getOid());
        roleDao.delete(role);
        return role;
    }

    @PUT
    @UnitOfWork
    @Path("group/{groupOid}/{roleOid}")
    public Role createRoleGroup(@Auth Credentials credentials, @PathParam("groupOid") String groupOid, @PathParam("roleOid") String roleOid) {
        Role group = roleDao.findById(groupOid);
        checkNotNull(group, "groupOid mustn't be null");
        Role role = roleDao.findById(roleOid);
        checkNotNull(role, "roleOid mustn't be null");

        boolean included = isRoleIncludedAsSubRole(role, groupOid);

        if (included)
            throw new RobeRuntimeException("", "aaa");

        if (group.getRoles() == null) {
            group.setRoles(new HashSet<Role>());
        }

        group.getRoles().add(role);

        roleDao.update(group);

        return group;
    }

    private boolean isRoleIncludedAsSubRole(Role role, String groupOid) {
        if (role.getOid().equals(groupOid))
            return true;
        for (Role child : role.getRoles()) {
            if (isRoleIncludedAsSubRole(child, groupOid))
                return true;
        }
        return false;
    }

    @DELETE
    @UnitOfWork
    @Path("destroyRoleGroup/{groupOid}/{roleOid}")
    public Role destroyRoleGroup(@Auth Credentials credentials, @PathParam("groupOid") String groupOid, @PathParam("roleOid") String roleOid) {
        Role group = roleDao.findById(groupOid);
        Role role = roleDao.findById(roleOid);

        if (group.getRoles() != null && group.getRoles().contains(role)) {
            group.getRoles().remove(role);
               roleDao.update(group);

        }

        return group;
    }
}
