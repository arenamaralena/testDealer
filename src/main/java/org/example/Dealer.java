package org.example;

/**
 * Проводит игру по правилам Техасского Холдема
 */
public interface Dealer {

    /**
     * Раздает карты двум игрокам на новой доске
     */
    Board dealCardsToPlayers();

    /**
     * Возвращает новую доску на основе имеющейся, докладывает 3 карты флопа из колоды
     */
    Board dealFlop(Board board);

    /**
     * Возвращает новую доску на основе имеющейся, докладывает 1 карту тёрна из колоды
     */
    Board dealTurn(Board board);

    /**
     * Возвращает новую доску на основе имеющейся, докладывает 1 карту ривера из колоды
     */
    Board dealRiver(Board board);

    /**
     * Определяет результат на готовой доске
     *
     * @throws InvalidPokerBoardException в случае отсутствия/некорректности каких-либо карт
     */
    PokerResult decideWinner(Board board) throws InvalidPokerBoardException;

}
