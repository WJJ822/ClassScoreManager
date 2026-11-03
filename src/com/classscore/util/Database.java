package com.classscore.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2 文件数据库。应用启动时自动建表并写入演示数据，无需安装 MySQL。
 * 数据文件默认保存在项目 data 目录，也可通过 -Dclassscore.home 或环境变量 CLASSSCORE_HOME 指定。
 */
public final class Database {

    private static final Logger LOG = Logger.getLogger(Database.class.getName());
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static volatile boolean initialized;
    private static Path dataDirectory;
    private static String jdbcUrl;

    private Database() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        try {
            Class.forName("org.h2.Driver");
            dataDirectory = resolveDataDirectory();
            Files.createDirectories(dataDirectory);
            String dbFile = dataDirectory.resolve("classscore").toAbsolutePath()
                    .toString().replace('\\', '/');
            jdbcUrl = "jdbc:h2:file:" + dbFile
                    + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE"
                    + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            createSchema();
            seedIfNeeded();
            initialized = true;
            LOG.info("班级成绩数据库已就绪：" + dataDirectory.toAbsolutePath());
        } catch (Exception ex) {
            String detail = ex.getMessage() == null ? "" : ex.getMessage();
            if (detail.contains("already in use") || detail.contains("locked")
                    || detail.contains("文件已锁定")) {
                throw new IllegalStateException(
                        "成绩数据库正被另一个 Tomcat 占用。请只运行一个实例："
                        + "先在 IDEA 中停止 Tomcat，或双击项目根目录的 stop.bat 停止独立服务，再重新启动。",
                        ex);
            }
            throw new IllegalStateException("数据库初始化失败：" + detail, ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new SQLException("数据库尚未初始化");
        }
        return DriverManager.getConnection(jdbcUrl, DB_USER, DB_PASSWORD);
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SHUTDOWN");
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "关闭 H2 数据库时出现异常", ex);
        } finally {
            initialized = false;
        }
    }

    private static Path resolveDataDirectory() {
        String configured = System.getProperty("classscore.home");
        if (isBlank(configured)) {
            configured = System.getenv("CLASSSCORE_HOME");
        }
        if (!isBlank(configured)) {
            return Paths.get(configured.trim()).resolve("data");
        }
        Path projectPath = Paths.get("D:/javaproject/ClassScoreManager");
        if (Files.isDirectory(projectPath)) {
            return projectPath.resolve("data");
        }
        return Paths.get(System.getProperty("user.home"), ".classscore", "data");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void createSchema() throws SQLException {
        List<String> ddl = new ArrayList<>();
        ddl.add("CREATE TABLE IF NOT EXISTS teacher ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(50) NOT NULL,"
                + "title VARCHAR(40) DEFAULT '任课教师')");
        ddl.add("CREATE TABLE IF NOT EXISTS course ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "code VARCHAR(20) NOT NULL UNIQUE,"
                + "name VARCHAR(60) NOT NULL,"
                + "credit DECIMAL(3,1) NOT NULL DEFAULT 3.0)");
        ddl.add("CREATE TABLE IF NOT EXISTS student ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "student_no VARCHAR(30) NOT NULL UNIQUE,"
                + "name VARCHAR(50) NOT NULL,"
                + "class_name VARCHAR(60) NOT NULL,"
                + "gender VARCHAR(10) DEFAULT '未填写',"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        ddl.add("CREATE TABLE IF NOT EXISTS score ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "student_id INT NOT NULL,"
                + "course_id INT NOT NULL,"
                + "exam_name VARCHAR(30) NOT NULL,"
                + "score DECIMAL(5,2) NOT NULL,"
                + "teacher_id INT,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "CONSTRAINT uk_score_student_course_exam UNIQUE (student_id, course_id, exam_name),"
                + "CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,"
                + "CONSTRAINT fk_score_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,"
                + "CONSTRAINT fk_score_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE SET NULL)");
        ddl.add("CREATE TABLE IF NOT EXISTS app_user ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "password_hash VARCHAR(255) NOT NULL,"
                + "role VARCHAR(20) NOT NULL,"
                + "ref_id INT NOT NULL,"
                + "display_name VARCHAR(50) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        ddl.add("CREATE INDEX IF NOT EXISTS idx_student_class ON student(class_name)");
        ddl.add("CREATE INDEX IF NOT EXISTS idx_score_student ON score(student_id)");
        ddl.add("CREATE INDEX IF NOT EXISTS idx_score_course ON score(course_id)");

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : ddl) {
                statement.execute(sql);
            }
        }
    }

    private static void seedIfNeeded() throws SQLException {
        try (Connection connection = getConnection()) {
            if (count(connection, "app_user") > 0) {
                return;
            }
            connection.setAutoCommit(false);
            try {
                seed(connection);
                connection.commit();
                LOG.info("已写入演示班级、学生、课程与成绩数据");
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static int count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void seed(Connection connection) throws SQLException {
        int[] teacherIds = new int[2];
        teacherIds[0] = insertTeacher(connection, "王思远", "副教授 · 软件工程系");
        teacherIds[1] = insertTeacher(connection, "李明悦", "讲师 · 计算机科学系");

        int[] courseIds = new int[]{
                insertCourse(connection, "MATH101", "高等数学", 4.0),
                insertCourse(connection, "ENG101", "大学英语", 3.0),
                insertCourse(connection, "CS101", "Java 程序设计", 3.5),
                insertCourse(connection, "CS102", "数据结构", 4.0)
        };

        String[] studentNos = {
                "20240101", "20240102", "20240103", "20240104", "20240105", "20240106",
                "20240107", "20240108", "20240201", "20240202", "20240203", "20240204",
                "20240205", "20240206", "20240207", "20240208"
        };
        String[] names = {
                "张梓涵", "李昊然", "王雨桐", "陈子墨", "刘思远", "赵一诺", "孙嘉懿", "周若曦",
                "吴俊熙", "郑欣怡", "冯亦辰", "蒋语彤", "韩沐阳", "沈书瑶", "许景铄", "何清越"
        };
        String[] classes = {
                "软件工程1班", "软件工程1班", "软件工程1班", "软件工程1班",
                "软件工程1班", "软件工程1班", "软件工程1班", "软件工程1班",
                "计算机科学2班", "计算机科学2班", "计算机科学2班", "计算机科学2班",
                "计算机科学2班", "计算机科学2班", "计算机科学2班", "计算机科学2班"
        };
        String[] genders = {
                "女", "男", "女", "男", "男", "女", "男", "女",
                "男", "女", "男", "女", "男", "女", "男", "女"
        };
        List<Integer> studentIds = new ArrayList<>();
        for (int i = 0; i < studentNos.length; i++) {
            studentIds.add(insertStudent(connection, studentNos[i], names[i], classes[i], genders[i]));
        }

        insertUser(connection, "teacher", PasswordUtil.hash("123456"),
                "teacher", teacherIds[0], "王思远");
        insertUser(connection, "teacher2", PasswordUtil.hash("123456"),
                "teacher", teacherIds[1], "李明悦");
        for (int i = 0; i < studentIds.size(); i++) {
            insertUser(connection, studentNos[i], PasswordUtil.hash("123456"),
                    "student", studentIds.get(i), names[i]);
        }

        String[] exams = {"期中考试", "期末考试"};
        String insertScore = "INSERT INTO score (student_id, course_id, exam_name, score, teacher_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertScore)) {
            for (int i = 0; i < studentIds.size(); i++) {
                for (int c = 0; c < courseIds.length; c++) {
                    for (int e = 0; e < exams.length; e++) {
                        int value = demoScore(i, c, e);
                        ps.setInt(1, studentIds.get(i));
                        ps.setInt(2, courseIds[c]);
                        ps.setString(3, exams[e]);
                        ps.setInt(4, value);
                        ps.setInt(5, teacherIds[(i + c) % 2]);
                        ps.addBatch();
                    }
                }
            }
            ps.executeBatch();
        }
    }

    private static int demoScore(int student, int course, int exam) {
        int base = 63 + Math.floorMod(student * 13 + course * 17, 30);
        int delta = Math.floorMod(student * 3 + course * 5 + exam * 7, 13) - 6;
        int value = base + delta;
        if (student == 4 && course == 1) {
            value = 52;
        }
        if (student == 10 && course == 2) {
            value = 56;
        }
        if (student == 13 && exam == 0) {
            value -= 11;
        }
        if (student == 7 && course == 0) {
            value = 59;
        }
        if (student == 2 && course == 3) {
            value = 97;
        }
        if (student == 11 && course == 2) {
            value = 95;
        }
        return Math.max(38, Math.min(99, value));
    }

    private static int insertTeacher(Connection connection, String name, String title) throws SQLException {
        String sql = "INSERT INTO teacher (name, title) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, title);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static int insertCourse(Connection connection, String code, String name, double credit)
            throws SQLException {
        String sql = "INSERT INTO course (code, name, credit) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setDouble(3, credit);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static int insertStudent(Connection connection, String no, String name,
                                     String className, String gender) throws SQLException {
        String sql = "INSERT INTO student (student_no, name, class_name, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, no);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.setString(4, gender);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static void insertUser(Connection connection, String username, String hash,
                                   String role, int refId, String displayName) throws SQLException {
        String sql = "INSERT INTO app_user (username, password_hash, role, ref_id, display_name) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.setInt(4, refId);
            ps.setString(5, displayName);
            ps.executeUpdate();
        }
    }
}
