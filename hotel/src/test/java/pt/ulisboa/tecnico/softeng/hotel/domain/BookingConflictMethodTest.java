package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookingConflictMethodTest extends RollbackTestAbstractClass {
    private final LocalDate arrival = new LocalDate(2016, 12, 19);
    private final LocalDate departure = new LocalDate(2016, 12, 24);
    private Booking booking;

    @Override
    public void populate4Test() {
        Hotel hotel = new Hotel("XPTO123", "Londres");
        Room room = new Room(hotel, "01", Room.Type.SINGLE);

        this.booking = new Booking(room, this.arrival, this.departure);
    }

    @Test
    public void argumentsAreConsistent() {
        Assert.assertFalse(this.booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 15)));
    }

    @Test
    public void noConflictBecauseItIsCancelled() {
        this.booking.cancel();
        Assert.assertFalse(this.booking.conflict(this.booking.getArrival(), this.booking.getDeparture()));
    }

    @Test(expected = HotelException.class)
    public void argumentsAreInconsistent() {
        this.booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9));
    }

    @Test
    public void argumentsSameDay() {
        Assert.assertTrue(this.booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 9)));
    }

    @Test
    public void arrivalAndDepartureAreBeforeBooked() {
        Assert.assertFalse(this.booking.conflict(this.arrival.minusDays(10), this.arrival.minusDays(4)));
    }

    @Test
    public void arrivalAndDepartureAreBeforeBookedButDepartureIsEqualToBookedArrival() {
        Assert.assertFalse(this.booking.conflict(this.arrival.minusDays(10), this.arrival));
    }

    @Test
    public void arrivalAndDepartureAreAfterBooked() {
        Assert.assertFalse(this.booking.conflict(this.departure.plusDays(4), this.departure.plusDays(10)));
    }

    @Test
    public void arrivalAndDepartureAreAfterBookedButArrivalIsEqualToBookedDeparture() {
        Assert.assertFalse(this.booking.conflict(this.departure, this.departure.plusDays(10)));
    }

    @Test
    public void arrivalIsBeforeBookedArrivalAndDepartureIsAfterBookedDeparture() {
        Assert.assertTrue(this.booking.conflict(this.arrival.minusDays(4), this.departure.plusDays(4)));
    }

    @Test
    public void arrivalIsEqualBookedArrivalAndDepartureIsAfterBookedDeparture() {
        Assert.assertTrue(this.booking.conflict(this.arrival, this.departure.plusDays(4)));
    }

    @Test
    public void arrivalIsBeforeBookedArrivalAndDepartureIsEqualBookedDeparture() {
        Assert.assertTrue(this.booking.conflict(this.arrival.minusDays(4), this.departure));
    }

    @Test
    public void arrivalIsBeforeBookedArrivalAndDepartureIsBetweenBooked() {
        Assert.assertTrue(this.booking.conflict(this.arrival.minusDays(4), this.departure.minusDays(3)));
    }

    @Test
    public void arrivalIsBetweenBookedAndDepartureIsAfterBookedDeparture() {
        Assert.assertTrue(this.booking.conflict(this.arrival.plusDays(3), this.departure.plusDays(6)));
    }

}
