package com.example.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.chat.ChatApplication;
import com.example.chat.entity.Users;
import com.example.chat.mapper.ContactMapper;
import com.example.chat.mapper.UsersMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ChatApplication.class)
public class ImportContact {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private ContactMapper contactsMapper;

    @Test
    public void test() {
        usersMapper.selectList(new QueryWrapper<Users>().eq("password", "123456"));
        Users user = new Users();
        user.setName("aa");
        user.setPassword("123456");
        user.setInsertTime(new Date());
        user.setInsertMan("system");
        user.setUserNumber("4354545");
        usersMapper.insert(user);
    }
    @SuppressWarnings("resource")
    @Test
    public void addContacts() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(11);

        List<Users> users = usersMapper.selectList(new QueryWrapper<Users>().eq("password", "123456"));

        for(int i = 0; i < 11; i++) {
            List<Users> userList;
            if(i != 10) {
                userList = users.subList(i*3000, (i+1)*3000);
            }else {
                userList = users.subList(30000, users.size());
            }
            int j = i+1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
                    try {
                        File file = new File("C:\\Users\\jiyupeng\\Desktop\\contacts\\contact_"+j+".sql");
                        if(!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int count = 0;
                        int userCount = 0;

                        for (Users user : userList) {
                            List<Users> contactList = usersMapper.selectList(new QueryWrapper<Users>().orderByAsc("RANDOM()").last("limit "+(int)(Math.random()*70+30)));
                            String sql = "";
                            for (Users contact : contactList) {
                                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                                String sqlTem = "INSERT INTO \"contact\" VALUES ('#{id}', '#{userId}', '#{contactId}', NOW(), '#{insertMan}', NULL, NULL);\r\n";
                                sql += sqlTem.replace("#{id}", uuid)
                                        .replace("#{userId}", user.getId())
                                        .replace("#{contactId}", contact.getId())
                                        .replace("#{insertMan}", "system");
                                count++;
                            }
                            fileOutputStream.write(sql.getBytes());
                            System.out.println(user.getName()+"_"+(++userCount)+"_"+count);
                        }
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("子线程"+Thread.currentThread().getName()+"正在完毕");
                }
            }).start();

        }
        System.out.println("-----------------------------=============================");
        latch.await();

    }
}

