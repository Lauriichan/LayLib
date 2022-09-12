package me.lauriichan.laylib.command;

import java.lang.reflect.Method;
import java.util.List;

import me.lauriichan.laylib.command.annotation.Description;
import me.lauriichan.laylib.command.annotation.Permission;
import me.lauriichan.laylib.reflection.ClassUtil;

public final class NodeAction {

    private final Method method;
    private final List<NodeArgument> arguments;

    private final String description;
    private final String permission;

    NodeAction(final Method method, final List<NodeArgument> arguments) {
        this.method = method;
        this.arguments = arguments;
        Description descriptionInfo = ClassUtil.getAnnotation(method, Description.class);
        this.description = descriptionInfo == null ? "N/A" : descriptionInfo.value();
        Permission permissionInfo = ClassUtil.getAnnotation(method, Permission.class);
        this.permission = permissionInfo == null ? null : permissionInfo.value();
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isRestricted() {
        return permission != null;
    }

    public Method getMethod() {
        return method;
    }

    public List<NodeArgument> getArguments() {
        return arguments;
    }
    
    public int getArgumentCount() {
        return arguments.size();
    }

}
