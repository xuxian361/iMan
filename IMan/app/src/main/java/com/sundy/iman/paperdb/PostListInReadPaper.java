package com.sundy.iman.paperdb;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * 保存post 列表已读状态/展开
 * Created by sundy on 17/10/29.
 */

public class PostListInReadPaper {

    /**
     * 保存已读的post ids
     */
    public static void saveReadId(String community_id, String post_id) {
        List<String> listReads = getPostReadIds(community_id);
        if (listReads == null) {
            listReads = new ArrayList<>();
        }
        if (listReads.size() > 0) {
            if (!listReads.contains(post_id)) {
                listReads.add(post_id);
            }
        } else {
            listReads.add(post_id);
        }

        Paper.book("post_read").write(community_id, listReads);
    }

    /**
     * 根据社区ID来清除已读的post记录
     */
    public static void deletePostReadIds(String community_id) {
        Paper.book("post_read").delete(community_id);
    }

    /**
     * 清除已读的post记录
     */
    public static void deleteAllPostReadIds() {
        Paper.book("post_read").destroy();
    }

    /**
     * 获取已读的post记录
     */
    public static List<String> getPostReadIds(String community_id) {
        return Paper.book("post_read").read(community_id, null);
    }

}
