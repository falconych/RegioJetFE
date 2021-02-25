package regiojet.exercise;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.codeborne.selenide.Selenide.*;


public class RegioJetExercise {
    // To get all available options starting next Monday
    LocalDateTime now = LocalDateTime.now();
    String patternWeekDay = "d MMM";
    String patternCheck = "d MMM yyyy";
    String ResultsCheck = "d MMMM yyy";
    // date checks

    String nextMon = calcNextMonday(now, patternWeekDay);
    String DateCheck = calcNextMonday(now, patternCheck);
    String ResultsCheckDate = calcNextMonday(now, ResultsCheck);


    String LocalizationDropDown = "//div[@id='header-locale']//span[@role='button']";
    String LanguageSelect = "//div[normalize-space()='English']";
    String SearchDateLocator ="//span[normalize-space()='" + ResultsCheckDate + "']";
    String SearchDateMonday ="//span[normalize-space()='Monday']";


    String TravelTime = "//span[contains(@class, 'travel-time')]";
    String ConnectionTime = "//span[contains(@class, 'text-regular')]";


    static String calcNextMonday(LocalDateTime d, String pattern) {
        LocalDateTime nextMondayDate = d.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        return nextMondayDate.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    @Test
    public void SetCityDeparture() {
        // open test link
        open("https://shop.regiojet.sk/");
        // select language
        $(By.xpath(LocalizationDropDown)).click();
        $(By.xpath(LanguageSelect)).click();
        // find departure place box and enter Ostrava
        $(By.id("route-from")).setValue("Ostrava").pressEnter();
        // assure that data is inserted
        $(By.id("react-select-route-from--value-item")).shouldBe(Condition.visible);
        // find destination place box and enter Brno
        $(By.id("route-to")).setValue("Brno").pressEnter();
        // assure that data is inserted
        $(By.id("react-select-route-to--value-item")).shouldBe(Condition.visible);

        // get next Monday string

        $(By.xpath("//button[@class='button']")).click();

        $(By.id("route-there-input")).setValue(nextMon).pressEnter();
        // check that date for next Monday is inserted


        $(By.id("route-there-input")).shouldHave(Condition.attribute("value", DateCheck));

        $(By.id("search-button")).click();

    }
    @Test
    public void SearchResults() {

        // Check that departure is on Monday searched date
        $(By.xpath(SearchDateMonday)).shouldBe(Condition.visible);
        $(By.xpath(SearchDateLocator)).shouldBe(Condition.visible);

       int connectionDetails = $$(By.xpath("//div[contains(@class, 'connection-detail')]")).size();

        System.out.println("There are " + connectionDetails + " possible connection operations.");


        // creating a list out of locator ConnectionTime by creating a list out of departure arrival times
        List<String> TimeList = $$(By.xpath(ConnectionTime)).texts();
        ArrayList <LocalTime> ArrivalTimeList = new ArrayList();
        DateTimeFormatter hourDF = DateTimeFormatter.ofPattern("H:mm");
        for (int i = 1; i < TimeList.size(); i+=2) {
            ArrivalTimeList.add(LocalTime.parse(TimeList.get(i),hourDF));
        }
        // sorting by time
        Collections.sort(ArrivalTimeList);

        // the fastest arrival time
        System.out.println("Fastest arrival time is: " + ArrivalTimeList.get(0));

        // get all string with travel time and parse it by substring and get actual travel time
        List<String> TravelTimeListRaw = $$(By.xpath(TravelTime)).texts();

        ArrayList <LocalTime> TravelTimeList = new ArrayList();
        for (String s : TravelTimeListRaw) {
            TravelTimeList.add(LocalTime.parse(s.substring(11, 16), hourDF));
        }
        // sorting by time
        Collections.sort(TravelTimeList);
        System.out.println("The shortest time spent with travelling: " + TravelTimeList.get(0));


        List<String> PriceListRaw = $$(By.id("price-yellow-desktop")).texts();
        ArrayList <Double>  PriceList = new ArrayList<>();
        for (String s : PriceListRaw) {
            s = s.replaceAll("[^\\.0123456789]","");
            PriceList.add(Double.parseDouble(s));
        }
        //sorting by price
        Collections.sort(PriceList);
        System.out.println("The lowest price of the journey: " + PriceList.get(0));

    }

}
