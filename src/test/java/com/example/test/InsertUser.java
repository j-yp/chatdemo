package com.example.test;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.chat.ChatApplication;
import com.example.chat.entity.Users;
import com.example.chat.mapper.UsersMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChatApplication.class)
public class InsertUser {
    @Autowired
    private UsersMapper usersMapper;

    @Test
    public void addUser() throws IOException {
        List<String> list = readLine();
        LinkedList<String> names = new LinkedList<>(list);
        /*String pop = names.pop();
        System.out.println(pop);
        String pop2 = names.pop();
        System.out.println(pop2);*/

        while (!names.isEmpty()) {
            String name = names.pop();
            Users user = new Users();
            user.setName(name);
            user.setPassword("123456");
            String userNumber = "";
            do {
                Random r = new Random();
                int i = r.nextInt(2000000000) + 300000;
                List<Users> list1 = usersMapper.selectList(new QueryWrapper<Users>().eq("user_number", i + ""));
                if (list1.isEmpty()) {
                    userNumber = i + "";
                    break;
                }
            } while (true);
            user.setUserNumber(userNumber);
            user.setInsertMan("system");
            user.setInsertTime(new Date());
            usersMapper.insert(user);
        }

    }

    private List<String> readLine() throws IOException {
        List<String> list = new ArrayList<>();
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\软件\\QQ\\2433887333\\FileRecv\\MobileFile\\names2.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int bytesRead = channel.read(buffer);
        ByteBuffer stringBuffer = ByteBuffer.allocate(20);
        int count = 0;
        while (bytesRead != -1) {
            System.out.println("读取字节数：" + bytesRead);
            //之前是写buffer，现在要读buffer
            buffer.flip();// 切换模式，写->读
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10 || b == 13) { // 换行或回车
                    stringBuffer.flip();
                    // 这里就是一个行
                    final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
                    if(line != null && !line.isEmpty() && !line.matches("\\S*[\\s]+\\S*") && !line.matches("\\S*[a-zA-Z]+\\S*")
                        && !line.contains("(") && !line.contains(")") && line.length() < 4){
                        System.out.println(line);// 解码已经读到的一行所对应的字节
                        list.add(line);
                        count++;
                    }
                    stringBuffer.clear();
                } else {
                    if (stringBuffer.hasRemaining())
                        stringBuffer.put(b);
                    else { // 空间不够扩容
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();// 清空,position位置为0，limit=capacity
            //  继续往buffer中写
            bytesRead = channel.read(buffer);
        }
        System.out.println("count: "+count);
        randomAccessFile.close();
        return list;
    }

    private ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }

}
