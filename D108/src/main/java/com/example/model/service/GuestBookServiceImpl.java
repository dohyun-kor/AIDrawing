package com.example.model.service;

import com.example.model.dao.GuestBookDao;
import com.example.model.dto.GuestBookDto;
import com.example.model.dto.GuestBookRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuestBookServiceImpl implements GuestBookService {

    private final GuestBookDao guestBookDao;

    @Autowired
    public GuestBookServiceImpl(GuestBookDao guestBookDao) {
        this.guestBookDao = guestBookDao;
    }

    @Override
    public int createGuestBook(GuestBookRequestDto guestBookRequestDto) {
        return guestBookDao.insertGuestBook(guestBookRequestDto);
    }

    @Override
    public int updateGuestBook(GuestBookDto guestBookDto) {
        return guestBookDao.updateGuestBook(guestBookDto);
    }
    @Override
    public int deleteGuestBook(int guestBookId) {
        return guestBookDao.deleteGuestBook(guestBookId);
    }
}
