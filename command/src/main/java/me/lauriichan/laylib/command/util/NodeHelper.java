package me.lauriichan.laylib.command.util;

import java.util.Comparator;

import me.lauriichan.laylib.command.ArgumentMap;
import me.lauriichan.laylib.command.EmptyArgumentMap;
import me.lauriichan.laylib.command.IArgumentMap;
import me.lauriichan.laylib.command.NodeArgument;
import me.lauriichan.laylib.command.annotation.Param;
import me.lauriichan.laylib.reflection.ClassUtil;

public final class NodeHelper {

    private NodeHelper() {
        throw new UnsupportedOperationException();
    }

    private static final Comparator<? super NodeArgument> SORTER = new Comparator<NodeArgument>() {
        @Override
        public int compare(NodeArgument o1, NodeArgument o2) {
            int rc = Boolean.compare(o1.isProvided(), o2.isProvided());
            if (rc != 0) {
                return rc;
            }
            return Integer.compare(o1.getIndex(), o2.getIndex());
        }
    };

    private static final Comparator<? super NodeArgument> SORTER_OPTIONAL_LAST = new Comparator<NodeArgument>() {
        @Override
        public int compare(NodeArgument o1, NodeArgument o2) {
            int rc = Boolean.compare(o1.isProvided(), o2.isProvided());
            if (rc != 0) {
                return rc;
            }
            rc = Boolean.compare(o1.isOptional(), o2.isOptional());
            if (rc != 0) {
                return rc;
            }
            return Integer.compare(o1.getIndex(), o2.getIndex());
        }
    };

    public static Comparator<? super NodeArgument> sorter() {
        return SORTER;
    }

    public static Comparator<? super NodeArgument> sorterOptionalLast() {
        return SORTER_OPTIONAL_LAST;
    }

    public static boolean isConstantArgument(Class<?> type) {
        return false;
    }

    public static IArgumentMap paramsToMap(Param[] params) {
        if (params.length == 0) {
            return EmptyArgumentMap.INSTANCE;
        }
        ArgumentMap map = new ArgumentMap();
        loop:
        for (Param param : params) {
            switch (param.type()) {
            case Param.TYPE_STRING:
                map.set(param.name(), param.stringValue());
                break;
            case Param.TYPE_BOOLEAN:
                map.set(param.name(), param.booleanValue());
                break;
            case Param.TYPE_BYTE:
                map.set(param.name(), param.byteValue());
                break;
            case Param.TYPE_SHORT:
                map.set(param.name(), param.shortValue());
                break;
            case Param.TYPE_INT:
                map.set(param.name(), param.intValue());
                break;
            case Param.TYPE_LONG:
                map.set(param.name(), param.longValue());
                break;
            case Param.TYPE_FLOAT:
                map.set(param.name(), param.floatValue());
                break;
            case Param.TYPE_DOUBLE:
                map.set(param.name(), param.doubleValue());
                break;
            case Param.TYPE_CLASS:
                map.set(param.name(), param.classValue());
                break;
            case Param.TYPE_STRING_ARRAY:
                map.set(param.name(), param.stringArrayValue());
                break;
            case Param.TYPE_BOOLEAN_ARRAY:
                map.set(param.name(), param.booleanArrayValue());
                break;
            case Param.TYPE_BYTE_ARRAY:
                map.set(param.name(), param.byteArrayValue());
                break;
            case Param.TYPE_SHORT_ARRAY:
                map.set(param.name(), param.shortArrayValue());
                break;
            case Param.TYPE_INT_ARRAY:
                map.set(param.name(), param.intArrayValue());
                break;
            case Param.TYPE_LONG_ARRAY:
                map.set(param.name(), param.longArrayValue());
                break;
            case Param.TYPE_FLOAT_ARRAY:
                map.set(param.name(), param.floatArrayValue());
                break;
            case Param.TYPE_DOUBLE_ARRAY:
                map.set(param.name(), param.doubleArrayValue());
                break;
            case Param.TYPE_CLASS_ARRAY:
                map.set(param.name(), param.classArrayValue());
                break;
            default:
                continue loop;
            }
        }
        if (map.isEmpty()) {
            return EmptyArgumentMap.INSTANCE;
        }
        return map;
    }

    public static Object nullValueFor(Class<?> argumentType) {
        if (ClassUtil.isPrimitiveType(argumentType)) {
            Class<?> complexType = ClassUtil.toComplexType(argumentType);
            if (Number.class.isAssignableFrom(complexType)) {
                return complexType.cast(0);
            }
            if (Boolean.class.isAssignableFrom(complexType)) {
                return complexType.cast(false);
            }
        }
        return null;
    }

}
