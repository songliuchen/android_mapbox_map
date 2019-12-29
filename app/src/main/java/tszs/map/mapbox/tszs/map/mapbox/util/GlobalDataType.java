package tszs.map.mapbox.tszs.map.mapbox.util;

/**
 * 类功能说明 全局数据类型(统一数据类型,跨数据库)
 */
public enum GlobalDataType {

    /**
     * "True"或"False"值(0或1值)
     */
    Boolean(1),

    /**
     * 字符型
     */
    Char(2),

    /**
     * 文本型
     */
    String(3),

    /**
     * 字符串
     */
    Text(4),

    /**
     * 日期型
     */
    Date(5),

    /**
     * 日期时间型
     */
    DateTime(6),

    /**
     * 值类型适用于要求使用大量有效的整数及小数位数并且没有舍入错误的财务计算
     */
    Decimal(7),

    /**
     * 双精度浮点数
     */
    Double(8),

    /**
     * 单精度浮点数字
     */
    Single(9),

    /**
     * int类型
     */
    Int(11),

    /**
     * 无符号int类型
     */
    UInt(14),


    /**
     * 二进制流类型
     */
    ByteArray(16),

    /**
     * 未定义
     */
    NoDefault(17);

    private final int globalDataType;

    public int getGlobalDataType() {
        return globalDataType;
    }

    private GlobalDataType(int globalDataType) {
        this.globalDataType = globalDataType;
    }
}
