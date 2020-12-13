package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.CityRepository;
import com.example.demo.Repository.GoodsRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Util.JwtTokenUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
public class TestController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private UserDao userDao;
    //这个可以验证JWT的Token是否过期，过期了就返回的是error
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity<?> Test(@RequestParam String name, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        System.out.println(token.split(" ")[1]);
        String na = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        System.out.println(na);
        return ResponseEntity.ok(na);
    }

    //录入货物信息，生成uuid
    //参数 货物名，目的地，目的收货人
    //返回值中包含物流uuid
    @RequestMapping(value = "/CreateOrder", method = RequestMethod.POST)
    public String createImgOrMessage(@RequestParam String goodname,
                                     @RequestParam String rlocal,
                                     @RequestParam String rpeople,
                                     HttpServletRequest request){
        String token = request.getHeader("Authorization");
        System.out.println(token.split(" ")[1]);
        String name = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        Goods good = new Goods();
        //这里判断人是否存在，否则返回失败
        System.out.println(rpeople);
        User user = userRepository.findUserBynickName(rpeople);
        if(user!=null){
            good.setGuid(UUID.randomUUID().toString());
            good.setGoodsName(goodname);
            good.setReceivelocal(rlocal);
            good.setReceivepeople(rpeople);
        }else{
            return "收货人不存在";
        }
        //发货人信息
        User user1 = userRepository.findUserBynickName(name);
        if(user1!=null){
            good.setSendpeople(name);
            good.setSendlocal(user1.getAddress());
        }else{
            return "发货人不存在";
        }
        good.setNowLocal(good.getSendlocal());
        //生成path，使用随机数来随机抽取城市，起点和重点不是随机的设置站点，不超过10
        Random random = new Random();
        try{
            StringBuilder sb = new StringBuilder();
            sb.append(good.getSendlocal()).append(":");
            int m = 0;
            int b = random.nextInt(10);
            m=b/2;
            for(int i = b;i>0;i--){
                Optional<City> c = cityRepository.findById(random.nextInt(701)+1);
                sb.append(c.get().getCityname()).append(":");
            }
            sb.append(good.getReceivelocal());
            //给path赋值
            good.setPath(sb.toString());
            //通过
            String[] cs = good.getPath().split(":");
            for(int i = 0;i<cs.length;i++){
                if(cs[i].equals(good.getNowLocal())&&i!=cs.length-1                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ){
                    good.setNextLocal(cs[i+1]);
                }
            }
        }catch (Exception e){
            System.out.println(e.getCause().toString());
            return "error";
        }
        goodsRepository.save(good);
        //生成path后拿到下一个站点赋值
        return "success and uuid = "+good.getGuid();
    }

    //登录后完善信息，不成功就一直调用此接口
    //这个是在登录后才能进行,也可更新信息
    @RequestMapping(value = "/GetInformation", method = RequestMethod.POST)
    public ResponseEntity<?> doInformation(@RequestParam String IdNumber,
                                @RequestParam String name,
                                @RequestParam String phoneNumber,
                                @RequestParam String address,
                                HttpServletRequest request){
        //这个接口主要是向用户表写入数据，拿到token的name，然后与user的name字段相同
        //不成功就继续调用此接口
        String token = request.getHeader("Authorization");
        System.out.println(token.split(" ")[1]);
        String nickName = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        //判断用户是否存在，存在就更新信息，上面的信息在客户端都是必填，不可为空
        for(User up : userRepository.findAll()){
            if(up.getNickName().equals(nickName)){
                //更新信息
//                userRepository.updateUser(IdNumber,name,address,phoneNumber,nickName);
                up.setIdNumber(IdNumber);
                up.setName(name);
                up.setAddress(address);
                up.setPhoneNumber(phoneNumber);
                up.setNickName(nickName);
                userRepository.save(up);
                return ResponseEntity.ok("success");
            }
        }
        User u = new User();
        u.setUuid(UUID.randomUUID().toString());
        u.setNickName(nickName);
        u.setName(name);
        u.setIdNumber(IdNumber);
        u.setPhoneNumber(phoneNumber);
        u.setAddress(address);
        if(UserComplete(u)){
            userRepository.save(u);
            return ResponseEntity.ok("success");
        }else{
            return ResponseEntity.ok("failed 重新输入并请求");
        }
    }

    //判断用户信息是否录入
    @RequestMapping(value = "/Information", method = RequestMethod.POST)
    public ResponseEntity<?> Information(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String nickName = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        if(userRepository.findUserBynickName(nickName)!=null){
            return ResponseEntity.ok("success");
        }else{
            return ResponseEntity.ok("用户未完善信息");
        }
    }


    //废弃接口，直接使用别人的二维码生成接口好了
    //在登录的状态下，由发货人调用此接口，如果不是发货人，就不允许调用
    @RequestMapping(value = "/getQRCode")
    public void returnQRcode(HttpServletResponse response, HttpServletRequest request,String key) throws Exception{
        String token = request.getHeader("Authorization");
        System.out.println(token.split(" ")[1]);
        String nickName = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        ServletOutputStream stream = null;
        stream = response.getOutputStream();
        response.setContentType("image/jpeg");
        for(Goods gd : goodsRepository.findAll()){
            if(gd.getSendpeople().equals(nickName)&&gd.getGuid().equals(key)){
                System.out.println(key);

                try {
                    //使用工具类生成二维码
                    String url = "http://api.qrserver.com/v1/create-qr-code/?size=300*300";
                    url = url + String.format("&data=%s", key);
                    final OkHttpClient client = new OkHttpClient();
                    final Request request1 = new Request.Builder()
                            .url(url)
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    InputStream in = response1.body().byteStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, len);
                    }
                    in.close();
                } catch (Exception e) {
                    e.getStackTrace();
                } finally {
                    if (stream != null) {
                        stream.flush();
                        System.out.println("二维码写入成功");
                        stream.close();
                    }else{
                        stream.print("error");
                    }
                }
            }else{
                stream.print("error");
            }
        }

    }


    //这个是要扫描二维码，传入唯一标识符，不同类型的人返回的值不同
    @RequestMapping(value = "/GetMessage", method = RequestMethod.POST)
    public ResponseEntity<?> getMessage(@RequestParam String key,
                             HttpServletRequest request){
        String token = request.getHeader("Authorization");
        System.out.println(new String(new Date().toString()) +token.split(" ")[1]);
        String nickName = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        //这里还要获得当前使用该接口的人
        //从这里获取已登录的人员的类型
        //0收件人，1发件人，2中间快递员
        String type = "";
        //数据库中一定会有当前这个人的登录信息
        type = userDao.findByUsername(nickName).getType();
        System.out.println(type);
        Goods goods = null;
        if(type.equals("0")){
            //在good表中，收件人为自己的记录，并且未签收的可查看
            //可以看到自己快递全部的信息,这里要修改，后期可能会返回的是list，同时还要加上nickname判断，不然普通用户之间是可以相互查询的
            goods = goodsRepository.findGoodByGuid(key);
            if(goods.getReceivepeople().equals(nickName)){
                goods.setGid(0);
                goods.setSendpeople(null);
                return ResponseEntity.ok(goods);
            }else{
                return ResponseEntity.ok("非收货人，不可查看");
            }
            //不可看的字段gid,sendpeople
        }else if(type.equals("1")){
            goods = goodsRepository.findGoodByGuid(key);
            if(goods.getSendpeople().equals(nickName)){
                goods.setGid(0);
                goods.setNextLocal(null);
                goods.setNowLocal(null);
                goods.setPath(null);
                goods.setReceivelocal(null);
                goods.setReceivepeople(null);
                return ResponseEntity.ok(goods);
            }else{
                return ResponseEntity.ok("非发货人，不可查看");
            }

            //不可看字段gid,nextlocal,nowlocal,path,receivelocal,receivepeople,
        }else if(type.equals("2")) {
            //扫描到，既为发货，更新状态，当前站点，以及下一站点，在当前站点中可以加入运输中
            //只能看到下一站信息，不论什么邮件
            //不可看字段gid,goods_name,path,receivelocal,receivepeople,sendlocal,sendpeople
            //这里需要注意，不是运输路径上的运输人员，无法查看货物，也就是要查看运输路径中是否有当前查看人员的地址
            //
            goods = goodsRepository.findGoodByGuid(key);
            String scanlocal = userRepository.findUserBynickName(nickName).getAddress();
            String[] locals = goods.getPath().split(":");
            int i = 0;
            int flage = 0;
            for(String l : locals){
                if(scanlocal.equals(l)){
                    //do_nothing
                    flage = 1;
                    break;
                }
            }
            if(flage == 1){
                //取得当前扫描的快递员的站点信息，也就是位置信息show
                User nowLocalUser = userRepository.findUserBynickName(nickName);
                String address = nowLocalUser.getAddress();
                //取得货物的对象
                String now_local = goods.getNextLocal();
                //如果当前的address地址和nowlocal地址相同就表示可以向下一站发送
                if(address.equals(now_local)){
                    //更新数据库的数据，主要是更新nextlocal和nowlocal
                    goods.setNowLocal(now_local);
                    String path = goods.getPath();
//                    String [] locals = path.split(":");
                    for(i = 0; i <=locals.length-1; i++){
                        if(locals[i].equals(goods.getNowLocal())&&locals.length-2>i){
                            goods.setNextLocal(locals[i+1]);
                            goodsRepository.save(goods);
                            break;
                        }else if(locals.length-1 == i){
                            goods.setNextLocal("null");
                            goodsRepository.save(goods);
                            break;
                        }
                    }
                }else{
                    return ResponseEntity.ok("快递信息错误，中间站点产生错误,当前所应在站点/下一站点应为："+goods.getNextLocal());
                }
//                goods.setNextLocal("");
//                这里的NowLocal可以多一个状态，运输中，在sendnext中产生“运输中”状态，在当下一站的快递员扫描到就可以继续更新状态
                //////这里更新状态时，nowlocal是运输中，将next的数据给now，同时找到next
//                goods.setNowLocal(goods.getNextLocal());
//                这里还要更新下一站
//                或者不在这里更新，直接新建一个接口，用于点击发送到下一站
//                goodsRepository.save(goods);
                goods.setGid(0);
                goods.setGoodsName(null);
                goods.setNextLocal(null);
                goods.setNowLocal(null);
                goods.setPath(null);
                goods.setReceivelocal(null);
                goods.setReceivepeople(null);
                goods.setSendpeople(null);
                goods.setSendlocal(null);

            }else{
//                goods = new Goods();
                return ResponseEntity.ok("快递员不再货物运输路径中，不可查看货物信息，当前所应在站点/下一站点应为："+goods.getNextLocal());
            }
            return ResponseEntity.ok(goods);

        }else{
            goods = new Goods();
            return ResponseEntity.ok(goods);
        }
    }


    //中间转运市发往下一个站点的函数，这个函数主要是将货物的nowlocal和nextlocal更新
    //由于在上面已经更新了数据，所以这里只需要将nowlocal改成运输中即可
    @RequestMapping(value = "/SendNext", method = RequestMethod.POST)
    public ResponseEntity<?> sendNext(@RequestParam String key, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        System.out.println(token.split(" ")[1]);
        String nickName = jwtTokenUtil.getUsernameFromToken(token.split(" ")[1]);
        //取得当前扫描的快递员的站点信息，也就是位置信息show
        User nowLocalUser = userRepository.findUserBynickName(nickName);
        String address = nowLocalUser.getAddress();
        //取得货物的对象
        Goods goods = goodsRepository.findGoodByGuid(key);
        String now_local = goods.getNowLocal();
        //如果当前的address地址和nowlocal地址相同就表示可以向下一站发送
        if(address.equals(now_local)){
            //更新数据库的数据，主要是更新nextlocal和nowlocal
            goods.setNowLocal("运输中");
            goodsRepository.save(goods);

        }else{
            return ResponseEntity.ok("无法发送，发送者地址错误，与当前货物地址不匹配");
        }
        return ResponseEntity.ok("发向下一站成功");
    }



    //判断user信息是否完整，辅助函数
    private boolean UserComplete(User u){

        if(u.getIdNumber()==null||u.getIdNumber().equals("")){
            return false;
        }else if(u.getName() == null||u.getIdNumber().equals("")){
            return false;
        }else if(u.getPhoneNumber() == null||u.getIdNumber().equals("")){
            return false;
        }else if(u.getAddress() == null||u.getIdNumber().equals("")){
            return false;
        }
        return true;
    }
}
