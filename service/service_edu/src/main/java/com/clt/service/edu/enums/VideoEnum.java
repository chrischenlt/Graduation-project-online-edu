package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum VideoEnum {

    COURSE_ID("course_id", "课程ID"),
    CHAPTER_ID("chapter_id","章节ID"),
    TITLE("title","节点名称"),
    VIDEO_SOURCE_ID("video_source_id","云端视频资源"),
    VIDEO_ORIGINAL_NAME("video_original_name","原始文件名称"),
    SORT("sort","排序字段"),
    PLAY_COUNT("play_count","播放次数"),
    FREE("free","是否可以试听：0收费 1免费"),
    DURATION("duration","视频时长（秒）"),
    STATUS("status","状态"),
    SIZE("size","视频源文件大小（字节）"),
    VERSION("version","乐观锁"),
    ;

    private final String column;
    private final String label;

    VideoEnum(String column, String label) {
        this.column = column;
        this.label = label;
    }

    public String getColumn() {
        return column;
    }

    public String getLabel() {
        return label;
    }
}
