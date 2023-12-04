package org.jda.example.coursemanrestful.utils;

import org.jda.example.coursemanrestful.modules.student.model.Gender;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @overview A toolkit class for the software.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DToolkit {
  
  public static final Date MIN_DOB = getTime(1,0,1900);

  private DToolkit() {}

  /**
   * @requires 
   * d in [0,31], m in [0,11]
   * 
   * @effects 
   *   return the Date object representing d/m/y.
   *   <br>Note: m starts from 0  
   */
  public static Date getTime(int d, int m, int y) {
    Calendar cal = Calendar.getInstance();
    cal.set(y, m, d);
    return cal.getTime();
  }

  private static Random rand;

  public static Random rand() {
    if (rand == null) {
      rand = new Random();
    }

    return rand;
  }

  public static String randCity() {
    String city = citiesDataSet[rand().nextInt(citiesDataSet.length)];
    String[] items = city.split(",");
    return items[0];
  }

  public static String randEmail() {
    String email = personsDataSet[rand().nextInt(personsDataSet.length)];
    String[] items = email.split(",");
    return items[2];
  }

  public static Date randDate(int minYear, int maxYear) {
    Random rand = rand();
    // YEAR
    int year = rand.nextInt(maxYear-minYear) + minYear;
    // month
    int max, min;
    max = 12; min = 1;
    int m = rand.nextInt(max - min) + min;
    // day
    max = 28; min = 1;
    int d = rand.nextInt(max - min) + min;

    return getTime(d, m, year);
  }

  public static Date randDob() {
    return randDate(1990,2000);
  }

  public static Gender randGender() {
    Gender[] range = {Gender.Male, Gender.Female};
    return range[rand().nextInt(range.length)];
  }

  public static String randName() {
    String person = personsDataSet[rand().nextInt(personsDataSet.length)];
    String[] items = person.split(",");
    return items[0] + " " + items[1];
  }

  private static final String[] personsDataSet = persons();

  private static final String[] citiesDataSet = citiesVn();

  private static String[] persons() {
    // First name, Last name, Email
    String persons = "Kelvin,Cunningham,k.cunningham@randatmail.com\n" +
        "Marcus,Armstrong,m.armstrong@randatmail.com\n" +
        "Adrian,Gibson,a.gibson@randatmail.com\n" +
        "Edith,Spencer,e.spencer@randatmail.com\n" +
        "Frederick,Moore,f.moore@randatmail.com\n" +
        "Alexander,Brown,a.brown@randatmail.com\n" +
        "Justin,Harris,j.harris@randatmail.com\n" +
        "Florrie,Jones,f.jones@randatmail.com\n" +
        "Kimberly,Moore,k.moore@randatmail.com\n" +
        "Aldus,Evans,a.evans@randatmail.com\n" +
        "John,Hill,j.hill@randatmail.com\n" +
        "Amber,Spencer,a.spencer@randatmail.com\n" +
        "Amelia,Elliott,a.elliott@randatmail.com\n" +
        "Tony,Spencer,t.spencer@randatmail.com\n" +
        "Frederick,Hunt,f.hunt@randatmail.com\n" +
        "Lily,Douglas,l.douglas@randatmail.com\n" +
        "William,Farrell,w.farrell@randatmail.com\n" +
        "Florrie,Hamilton,f.hamilton@randatmail.com\n" +
        "Emily,Murray,e.murray@randatmail.com\n" +
        "Gianna,Morris,g.morris@randatmail.com\n" +
        "Lilianna,Johnson,l.johnson@randatmail.com\n" +
        "Jenna,Anderson,j.anderson@randatmail.com\n" +
        "Jared,Gibson,j.gibson@randatmail.com\n" +
        "Owen,Lloyd,o.lloyd@randatmail.com\n" +
        "Aston,Roberts,a.roberts@randatmail.com\n" +
        "Rebecca,Johnson,r.johnson@randatmail.com\n" +
        "Stuart,Douglas,s.douglas@randatmail.com\n" +
        "Amanda,Anderson,a.anderson@randatmail.com\n" +
        "Justin,Williams,j.williams@randatmail.com\n" +
        "Grace,Grant,g.grant@randatmail.com";

    return persons.split("\\n");
  }

  private static String[] citiesVn() {
    // city,lat,lng,country,iso2,admin_name,capital,population,population_proper
    String cities = "Ho Chi Minh City,10.7756,106.7019,Vietnam,VN,Hồ Chí Minh,admin,15136000,7431000\n" +
        "Hanoi,21.0283,105.8542,Vietnam,VN,Hà Nội,primary,8246600,8246600\n" +
        "Haiphong,20.8651,106.6838,Vietnam,VN,Hải Phòng,admin,2103500,2103500\n" +
        "Cần Thơ,10.0333,105.7833,Vietnam,VN,Cần Thơ,admin,1237300,1237300\n" +
        "Biên Hòa,10.9500,106.8167,Vietnam,VN,Đồng Nai,admin,1104000,1104000\n" +
        "Thủ Đức,10.8266,106.7609,Vietnam,VN,Hồ Chí Minh,minor,1013795,1013795\n" +
        "Huế,16.4667,107.5792,Vietnam,VN,Thừa Thiên-Huế,admin,652572,652572\n" +
        "Tân An,10.9050,106.6994,Vietnam,VN,Hồ Chí Minh,,618984,618984\n" +
        "Bắc Ninh,21.1833,106.0500,Vietnam,VN,Bắc Ninh,admin,520000,520000\n" +
        "Hải Dương,20.9397,106.3306,Vietnam,VN,Hải Dương,admin,507469,507469\n" +
        "Vinh,18.6667,105.6667,Vietnam,VN,Nghệ An,admin,490000,490000\n" +
        "Thanh Hóa,19.8075,105.7764,Vietnam,VN,Thanh Hóa,admin,393294,393294\n" +
        "Nam Định,20.4200,106.1683,Vietnam,VN,Nam Định,admin,352108,352108\n" +
        "Long Xuyên,10.3736,105.4458,Vietnam,VN,An Giang,admin,278658,278658\n" +
        "Việt Trì,21.3000,105.4333,Vietnam,VN,Phú Thọ,admin,277539,277539\n" +
        "Mỹ Tho,10.3500,106.3500,Vietnam,VN,Tiền Giang,admin,270700,270700\n" +
        "Thái Bình,20.4461,106.3422,Vietnam,VN,Thái Bình,admin,268167,268167\n" +
        "Sa Đéc,10.3000,105.7667,Vietnam,VN,Đồng Tháp,minor,213610,213610\n" +
        "Phủ Từ Sơn,21.1189,105.9611,Vietnam,VN,Bắc Ninh,,202874,202874\n" +
        "Bắc Giang,21.2667,106.2000,Vietnam,VN,Bắc Giang,admin,201595,201595\n" +
        "Vĩnh Long,10.2500,105.9667,Vietnam,VN,Vĩnh Long,admin,200120,200120\n" +
        "Lạng Sơn,21.8478,106.7578,Vietnam,VN,Lạng Sơn,admin,200108,200108\n" +
        "Sầm Sơn,19.7333,105.9000,Vietnam,VN,Thanh Hóa,,172350,172350\n" +
        "Ninh Bình,20.2539,105.9750,Vietnam,VN,Ninh Bình,admin,160166,160166\n" +
        "Vĩnh Yên,21.3100,105.5967,Vietnam,VN,Vĩnh Phúc,admin,152801,152801\n" +
        "Long Bình,10.9458,106.8775,Vietnam,VN,Đồng Nai,,133206,133206\n" +
        "Hội An,15.8833,108.3333,Vietnam,VN,Quảng Nam,,121716,121716\n" +
        "Bình Hòa,10.9061,106.7308,Vietnam,VN,Bình Dương,,88500,88500\n" +
        "Cửa Lô,18.8167,105.7167,Vietnam,VN,Nghệ An,,75260,75260\n" +
        "Tam Hiệp,10.9497,106.8575,Vietnam,VN,Đồng Nai,,35747,35747\n" +
        "Bửu Long,10.9600,106.7967,Vietnam,VN,Đồng Nai,,31861,31861\n" +
        "Quận Mười Một,10.7638,106.6436,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Năm,10.7557,106.6675,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Mười,10.7682,106.6663,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Phú Nhuận,10.7948,106.6763,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Sáu,10.7468,106.6490,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Đống Đa,21.0201,105.8310,Vietnam,VN,Hà Nội,minor,,\n" +
        "Hai BàTrưng,21.0134,105.8477,Vietnam,VN,Hà Nội,minor,,\n" +
        "Quận Ba,10.7749,106.6863,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Hoàn Kiếm,21.0286,105.8506,Vietnam,VN,Hà Nội,minor,,\n" +
        "Quận Một,10.7807,106.6994,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Bốn,10.7668,106.7057,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Cầu Giấy,21.0323,105.8007,Vietnam,VN,Hà Nội,minor,,\n" +
        "Quận Tân Phú,10.7838,106.6370,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Quận Bình Thạnh,10.8033,106.6967,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Ba Đình,21.0341,105.8145,Vietnam,VN,Hà Nội,minor,,\n" +
        "Thanh Khê,16.0706,108.1910,Vietnam,VN,Đà Nẵng,minor,,\n" +
        "Thanh Xuân,20.9947,105.7998,Vietnam,VN,Hà Nội,minor,,\n" +
        "Đà Nẵng,16.0748,108.2240,Vietnam,VN,Đà Nẵng,admin,,\n" +
        "Quận Bảy,10.7118,106.7364,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Tây Hồ,21.0689,105.8105,Vietnam,VN,Hà Nội,minor,,\n" +
        "Lái Thiêu,10.9051,106.6995,Vietnam,VN,Bình Dương,minor,,\n" +
        "Hà Đông,20.9714,105.7788,Vietnam,VN,Hà Nội,minor,,\n" +
        "Cầu Diễn,21.0393,105.7666,Vietnam,VN,Hà Nội,minor,,\n" +
        "Quận Hai,10.7916,106.7495,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Hóc Môn,10.8886,106.5958,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Cẩm Lệ,16.0177,108.2038,Vietnam,VN,Đà Nẵng,minor,,\n" +
        "Quận Chín,10.8397,106.7709,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Văn Điển,20.9463,105.8450,Vietnam,VN,Hà Nội,minor,,\n" +
        "Sơn Trà,16.0607,108.2326,Vietnam,VN,Đà Nẵng,minor,,\n" +
        "Trâu Quỳ,21.0195,105.9369,Vietnam,VN,Hà Nội,minor,,\n" +
        "Trôi,21.0695,105.7065,Vietnam,VN,Hà Nội,minor,,\n" +
        "Phùng,21.0890,105.6590,Vietnam,VN,Hà Nội,minor,,\n" +
        "Tân Túc,10.6954,106.5913,Vietnam,VN,Hồ Chí Minh,minor,,\n" +
        "Đông Anh,21.1550,105.8486,Vietnam,VN,Hà Nội,minor,,";

    return cities.split("\\n");
  }
}
