package org.freelectron.leobel.winline98.services;

import org.freelectron.winline.LogicWinLine;

import java.util.List;

/**
 * Created by leobel on 10/7/16.
 */
public interface GameService {

    Boolean save(LogicWinLine game, int time);
    Boolean remove(Long id);
    Boolean remove(List<Long> ids);
    LogicWinLine findById(Long id);
    List<LogicWinLine> findAll();
}
