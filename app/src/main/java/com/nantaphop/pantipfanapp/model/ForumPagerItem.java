package com.nantaphop.pantipfanapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@Table(name = "Forums")
public class ForumPagerItem extends Model implements Serializable {

    @Column
    public String title;
    @Column
    public String desc;
    @Column
    public String url;
    @Column
    public int position;
    @Column
    public boolean enable;

    public ForumPagerItem() {
    }

    public ForumPagerItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public static List<ForumPagerItem> getAll() {
        return getAll("");
    }

    public static List<ForumPagerItem> getAll(String where) {
        List<ForumPagerItem> list = new Select()
                .from(ForumPagerItem.class)
                .orderBy("position ASC")
                .where(where)
                .execute();
        if (list.size() == 0) {
            init();
            list = getAll(where);
        }
        return list;
    }

    @Override
    public String toString() {
        return title;
    }

    private static void init() {
        ForumPagerItem f;

        f = new ForumPagerItem();
        f.title = "Pantip Pick";
        f.desc = "รายการกระทู้น่าสนใจที่ถูกคัดเลือกโดยทีมงาน";
        f.url = "pantippick";
        f.position = 0;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "Pantip Trend";
        f.desc = "กระทู้ที่ได้รับความนิยมมากที่สุดใน 24 ชั่วโมงที่ผ่านมา";
        f.url = "pantiptrend";
        f.position = 1;
        f.enable = true;
        f.save();

        f = new ForumPagerItem();
        f.title = "กล้อง";
        f.desc = "กล้องถ่ายรูป กล้อง DSLR กล้องวิดีโอ เทคนิคการถ่ายรูป";
        f.url = "camera";
        f.position = 2;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ก้นครัว";
        f.desc = "ร้านอาหาร สูตรอาหาร อาหารคาว อาหารหวาน เบเกอรี่ ไอศกรีม";
        f.url = "food";
        f.position = 3;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "แกลเลอรี่";
        f.desc = "ภายถ่ายบุคคล ภาพถ่ายทิวทัศน์ ภาพถ่ายมาโคร";
        f.url = "gallery";
        f.position = 4;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ไกลบ้าน";
        f.desc = "เรียนต่อต่างประเทศ ทำงานต่างประเทศ วีซ่า";
        f.url = "klaibann";
        f.position = 5;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "จตุจักร";
        f.desc = "สัตว์เลี้ยง สุนัข แมว ต้นไม้ จัดสวน ของสะสม งานฝีมือ เกษตรกรรม";
        f.url = "jatujak";
        f.position = 6;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "เฉลิมกรุง";
        f.desc = "นักร้องนักดนตรี เพลง เครื่องดนตรี คอนเสิร์ต มิวสิควิดีโอ";
        f.url = "chalermkrung";
        f.position = 7;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "เฉลิมไทย";
        f.desc = "นักแสดง ภาพยนตร์ รายการโทรทัศน์ ละคร โฆษณาโทรทัศน์";
        f.url = "chalermthai";
        f.position = 8;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ชานเรือน";
        f.desc = "ครอบครัว ตั้งครรภ์ ตั้งชื่อลูก การเลี้ยงลูก การสอนลูก";
        f.url = "family";
        f.position = 9;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ชายคา";
        f.desc = "บ้าน คอนโดมิเนียม ตกแต่งบ้าน เฟอร์นิเจอร์ เครื่องใช้ไฟฟ้า เครื่องครัว";
        f.url = "home";
        f.position = 10;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ซิลิคอนวัลเลย์";
        f.desc = "คอมมือใหม่ อินเทอร์เน็ต ซอฟต์แวร์ ฮาร์ดแวร์ เกม เขียนโปรแกรม Gadget";
        f.url = "siliconvalley";
        f.position = 11;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "โต๊ะเครื่องแป้ง";
        f.desc = "เครื่องสำอาง เสริมสวย แฟชั่น เครื่องประดับ ลดความอ้วน";
        f.url = "beauty";
        f.position = 12;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ถนนนักเขียน";
        f.desc = "แต่งนิยาย เรื่องสั้น กลอน นิทาน";
        f.url = "writer";
        f.position = 13;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "บลูแพลนเน็ต";
        f.desc = "เที่ยวไทย เที่ยวต่างประเทศ ทะเล ภูเขา เกาะ น้ำตก ดำน้ำ สายการบิน";
        f.url = "blueplanet";
        f.position = 14;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "พันทิป";
        f.desc = "ข้อเสนอแนะถึงพันทิป วิธีการใช้งานพันทิป กิจกรรมพันทิป";
        f.url = "pantip";
        f.position = 15;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ภูมิภาค";
        f.desc = "ภาคเหนือ ภาคอีสาน ภาคกลาง ภาคตะวันออก ภาคตะวันตก ภาคใต้";
        f.url = "region";
        f.position = 16;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "มาบุญครอง";
        f.desc = "โทรศัพท์มือถือ Smartphone Tablet iOS Android";
        f.url = "mbk";
        f.position = 17;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "รัชดา";
        f.desc = "รถยนต์ มอเตอร์ไซค์ เครื่องเสียงรถยนต์ แต่งรถ การจราจร";
        f.url = "ratchada";
        f.position = 18;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ราชดำเนิน";
        f.desc = "การเมือง รัฐศาสตร์ กฎหมาย สภาผู้แทน รัฐบาล ฝ่ายค้าน พรรคการเมือง";
        f.url = "rajdumnern";
        f.position = 19;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ไร้สังกัด";
        f.desc = "กระทู้อื่นๆ ที่ไม่สังกัดห้องไหนเลย";
        f.url = "isolate";
        f.position = 20;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ศาลาประชาคม";
        f.desc = "กฎหมาย ปัญหาสังคม ปัญหาชีวิต เศรษฐกิจ คุ้มครองผู้บริโภค";
        f.url = "social";
        f.position = 21;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ศาสนา";
        f.desc = "ศาสนาพุทธ ศาสนาคริสต์ ศาสนาอิสลาม เที่ยววัด ทำบุญ";
        f.url = "religious";
        f.position = 22;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ศุภชลาศัย";
        f.desc = "กีฬา ฟุตบอล บาสเกตบอล มวยสากล กอล์ฟ จักรยาน นักกีฬา";
        f.url = "supachalasai";
        f.position = 23;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "สยามสแควร์";
        f.desc = "ชีวิตวัยรุ่น การเรียน โรงเรียน มหาวิทยาลัย ความรักวัยรุ่น เกม";
        f.url = "siam";
        f.position = 24;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "สวนลุมพินี";
        f.desc = "สุขภาพกาย สุขภาพจิต โรคมะเร็ง โรคไมเกรน โรคภูมิแพ้ ปวดประจำเดือน";
        f.url = "lumpini";
        f.position = 25;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "สินธร";
        f.desc = "หุ้น เศรษฐกิจ การลงทุน LTF RMF ธนาคาร เงินตราต่างประเทศ";
        f.url = "sinthorn";
        f.position = 26;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "สีลม";
        f.desc = "การบริหารจัดการ การตลาด ทรัพยากรบุคคล งานขาย SME ภาษีนิติบุคคล";
        f.url = "silom";
        f.position = 27;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "หว้ากอ";
        f.desc = "วิทยาศาสตร์ วิศวกรรม เทคโนโลยี ฟิสิกส์ ดาราศาสตร์ อวกาศ";
        f.url = "wahkor";
        f.position = 28;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "ห้องสมุด";
        f.desc = "หนังสือ หนังสือนิยาย ภาษาไทย ภาษาจีน ภาษาอังกฤษ ปรัชญา ประวัติศาสตร์";
        f.url = "library";
        f.position = 29;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "กรีนโซน";
        f.desc = "อนุรักษ์สิ่งแวดล้อม อนุรักษ์พลังงาน Green Living การออกแบบเพื่อสิ่งแวดล้อม ผลิตภัณฑ์รักษ์โลก เกษตรอินทรีย์";
        f.url = "greenzone";
        f.position = 30;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "หอศิลป์";
        f.desc = "ศิลปะ ภาพวาด ประวัติศาสตร์ศิลป์ สื่อประสม Graphic Design";
        f.url = "art";
        f.position = 31;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "การ์ตูน";
        f.desc = "การ์ตูนญี่ปุ่น การ์ตูนไทย การ์ตูนฝรั่ง อนิเมะ วาดการ์ตูน ของสะสมจากการ์ตูน คอสเพลย์";
        f.url = "cartoon";
        f.position = 32;
        f.enable = true;
        f.save();
        f = new ForumPagerItem();
        f.title = "บางขุนพรหม";
        f.desc = "ละคร นักแสดง ซีรี่ส์ รายการโทรทัศน์ สถานีโทรทัศน์";
        f.url = "tvshow";
        f.position = 33;
        f.enable = true;
        f.save();


    }


}
