package pt.ulisboa.tecnico.softeng.hotel.services.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;

public class HotelInterfaceGetRoomBookingDataMethodTest extends RollbackTestAbstractClass {
    private final LocalDate arrival = new LocalDate(2016, 12, 19);
    private final LocalDate departure = new LocalDate(2016, 12, 24);
    private Hotel hotel;
    private Room room;
    private Booking booking;

    @Override
    public void populate4Test() {
        this.hotel = new Hotel("XPTO123", "Lisboa");
        this.room = new Room(this.hotel, "01", Type.SINGLE);
        this.booking = this.room.reserve(Type.SINGLE, this.arrival, this.departure);
    }

    @Test
    public void success() {
        RoomBookingData data = HotelInterface.getRoomBookingData(this.booking.getReference());

        assertEquals(this.booking.getReference(), data.getReference());
        assertNull(data.getCancellation());
        assertNull(data.getCancellationDate());
        assertEquals(this.hotel.getName(), data.getHotelName());
        assertEquals(this.hotel.getCode(), data.getHotelCode());
        assertEquals(this.room.getNumber(), data.getRoomNumber());
        assertEquals(this.room.getType().name(), data.getRoomType());
        assertEquals(this.booking.getArrival(), data.getArrival());
        assertEquals(this.booking.getDeparture(), data.getDeparture());
    }

    @Test
    public void successCancellation() {
        this.booking.cancel();
        RoomBookingData data = HotelInterface.getRoomBookingData(this.booking.getCancellation());

        assertEquals(this.booking.getReference(), data.getReference());
        assertEquals(this.booking.getCancellation(), data.getCancellation());
        assertEquals(this.booking.getCancellationDate(), data.getCancellationDate());
        assertEquals(this.hotel.getName(), data.getHotelName());
        assertEquals(this.hotel.getCode(), data.getHotelCode());
        assertEquals(this.room.getNumber(), data.getRoomNumber());
        assertEquals(this.room.getType().name(), data.getRoomType());
        assertEquals(this.booking.getArrival(), data.getArrival());
        assertEquals(this.booking.getDeparture(), data.getDeparture());
    }

    @Test(expected = HotelException.class)
    public void nullReference() {
        HotelInterface.getRoomBookingData(null);
    }

    @Test(expected = HotelException.class)
    public void emptyReference() {
        HotelInterface.getRoomBookingData("");
    }

    @Test(expected = HotelException.class)
    public void referenceDoesNotExist() {
        HotelInterface.getRoomBookingData("XPTO");
    }

}
