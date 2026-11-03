package com.classscore.listener;

import com.classscore.util.Database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * 应用启动时初始化 H2 数据库，关闭时安全释放。
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Database.initialize();
        sce.getServletContext().setAttribute("classscore.dataDir", Database.getDataDirectory());
        LOG.info("班级学生成绩管理系统启动完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Database.shutdown();
    }
}
