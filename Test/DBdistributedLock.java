package com.java.Test;

/**
 * @author: Xyong
 * @since: 2019/6/5
 */
public class DBdistributedLock {

    private DataSource dataSource;

    private static final String cmd = "select * from lock where id = 1 for update";

    public DBdistributedLock(DataSource ds) {
        this.dataSource = ds;
    }

    public static interface CallBack{
        public void doAction();
    }
    public void lock(CallBack callBack)  {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            //3.1try get lock
            System.out.println(Thread.currentThread().getName() + " begin try lock");
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(cmd);
            rs = stmt.executeQuery();

            //3.2do business thing
            callBack.doAction();

            //3.3release lock
            conn.commit();
            System.out.println(Thread.currentThread().getName() + " release lock");

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            //3.4
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
