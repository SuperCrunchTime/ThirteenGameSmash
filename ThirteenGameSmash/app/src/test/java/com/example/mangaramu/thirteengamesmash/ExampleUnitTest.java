package com.example.mangaramu.thirteengamesmash;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    ThirteenGameLogic gl = new ThirteenGameLogic();
    AI cpu = new AI();
    HostHuman human = new HostHuman();

    @Test
    public void singleIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);

        assertEquals("Single: should be true", true, gl.isSingle(cards));
    }

    @Test
    public void singleIsIncorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(13);

        assertEquals("Single: should be false", false, gl.isSingle(cards));
    }

    @Test
    public void doubleIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(2);

        assertEquals("Double: should be true", true, gl.isDouble(cards));
    }

    @Test
    public void doubleIsIncorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(13);

        assertEquals("Double: should be false", false, gl.isDouble(cards));
    }

    @Test
    public void tripleIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(2);
        cards.add(3);

        assertEquals("Triple: should be true", true, gl.isTriple(cards));
    }

    @Test
    public void tripleIsIncorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(2);
        cards.add(13);

        assertEquals("Triple: should be false", false, gl.isTriple(cards));
    }

    @Test
    public void straightIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(4);
        cards.add(8);
        cards.add(12);
        cards.add(16);
        cards.add(20);
        cards.add(26);
        cards.add(30);
        cards.add(32);
        cards.add(37);
        cards.add(42);
        cards.add(47);

        assertEquals("Straight: should be true", true, gl.isStraight(cards));
    }

    @Test
    public void straightIsIncorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(13);
        cards.add(48);

        assertEquals("Straight: should be false", false, gl.isStraight(cards));
    }

    @Test
    public void hasDoubleIsNotStraight() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(1);
        cards.add(2);
        cards.add(4);
        cards.add(8);

        assertEquals("Straight: should be false", false, gl.isStraight(cards));
    }

    @Test
    public void bombIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(0);
        cards.add(1);

        cards.add(4);
        cards.add(5);

        cards.add(8);
        cards.add(9);

        cards.add(12);
        cards.add(13);

        cards.add(16);
        cards.add(19);

        cards.add(20);
        cards.add(21);

        assertEquals("Bomb: should be true", true, gl.isBomb(cards));
    }

    @Test
    public void bombIsIncorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.add(0);
        cards.add(1);
        cards.add(2);
        cards.add(5);
        cards.add(8);
        cards.add(9);

        assertEquals("Bomb: should be false", false, gl.isBomb(cards));
    }

    @Test
    public void hasHigherValueIsCorrect() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        cards.add(0);
        cards.add(1);
        cards.add(7);
        cards.add(5);
        cards.add(8);
        cards.add(11);

        playedCards.add(2);
        playedCards.add(3);
        playedCards.add(6);
        playedCards.add(4);
        playedCards.add(10);
        playedCards.add(9);

        assertEquals("HigherValue: should be true", true, gl.hasHigherValue(playedCards, cards));
    }

    @Test
    public void singleSameNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        cards.add(2);

        assertEquals("Single Same Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void singleDifferentNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        cards.add(40);

        assertEquals("Single Different Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void singleSameNumberIsNotPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(3);
        cards.add(2);

        assertEquals("Single Same Number is Playable: should be false", false, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void singleDifferentNumberIsNotPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(8);
        cards.add(2);

        assertEquals("Single Different Number is Playable: should be false", false, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void doubleSameNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(2);
        cards.add(1);
        cards.add(3);

        assertEquals("Double Same Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void doubleDifferentNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(2);
        cards.add(4);
        cards.add(5);

        assertEquals("Double Different Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void doubleSameNumberIsNotPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(3);
        cards.add(1);
        cards.add(2);

        assertEquals("Double Same Number is Playable: should be false", false, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void doubleDifferentNumberIsNotPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(40);
        playedCards.add(41);
        cards.add(1);
        cards.add(2);

        assertEquals("Double Different Number is Playable: should be false", false, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void tripleIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(2);
        playedCards.add(3);
        cards.add(6);
        cards.add(4);
        cards.add(5);

        assertEquals("Triple Is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void tripleIsNotPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(4);
        playedCards.add(5);
        playedCards.add(6);
        cards.add(1);
        cards.add(2);
        cards.add(3);

        assertEquals("Triple is Not Playable: should be false", false, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void straightSameStartNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(6);
        playedCards.add(8);
        playedCards.add(14);
        playedCards.add(19);
        playedCards.add(22);
        cards.add(1);
        cards.add(4);
        cards.add(10);
        cards.add(12);
        cards.add(16);
        cards.add(23);

        assertEquals("Straight Same Start Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void straightDifferentStartNumberIsPlayable() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(0);
        playedCards.add(6);
        playedCards.add(8);
        playedCards.add(14);
        playedCards.add(19);
        playedCards.add(22);
        cards.add(4);
        cards.add(9);
        cards.add(12);
        cards.add(16);
        cards.add(20);
        cards.add(24);

        assertEquals("Straight Same Start Number is Playable: should be true", true, gl.checkCardsSelected(playedCards, cards));
    }

    @Test
    public void singlesAIShouldPlay4() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        expectedPlayPile.add(4);
        hand.add(4);
        hand.add(9);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Singles AI should play 4", expectedPlayPile, playPile);
    }

    @Test
    public void singlesAIShouldPass() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(13);
        hand.add(4);
        hand.add(9);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Singles AI should pass", expectedPlayPile, playPile);
    }

    @Test
    public void singlesAIShouldPlay3OfHearts() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(2);
        expectedPlayPile.add(3);
        hand.add(3);
        hand.add(1);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Singles AI should play 3 of Hearts", expectedPlayPile, playPile);
    }

    @Test
    public void singlesSameValueAIShouldPass() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(3);
        hand.add(0);
        hand.add(2);
        hand.add(1);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Singles Same Value AI should pass", expectedPlayPile, playPile);
    }

    @Test
    public void doublesAIShouldPlayTwo3s() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        playPile.add(2);
        expectedPlayPile.add(1);
        expectedPlayPile.add(3);
        hand.add(1);
        hand.add(3);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Doubles AI should play Two 3s", expectedPlayPile, playPile);
    }

    @Test
    public void doublesAIShouldPlayTwo5s() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        playPile.add(2);
        expectedPlayPile.add(8);
        expectedPlayPile.add(10);
        hand.add(10);
        hand.add(8);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Doubles AI should play Two 5s", expectedPlayPile, playPile);
    }

    @Test
    public void triplesAIShouldPlayThree6s() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        playPile.add(3);
        playPile.add(2);
        expectedPlayPile.add(13);
        expectedPlayPile.add(14);
        expectedPlayPile.add(15);
        hand.add(13);
        hand.add(15);
        hand.add(14);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Triples AI should play Three 6s", expectedPlayPile, playPile);
    }

    @Test
    public void straightsAIShouldPlay34And5ofHearts() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        playPile.add(5);
        playPile.add(10);
        expectedPlayPile.add(1);
        expectedPlayPile.add(4);
        expectedPlayPile.add(11);
        hand.add(1);
        hand.add(4);
        hand.add(11);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Straights AI should play 3, 4, and then 5 of Hearts", expectedPlayPile, playPile);
    }

    @Test
    public void straightsAIShouldPlay567() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(0);
        playPile.add(5);
        playPile.add(10);
        expectedPlayPile.add(8);
        expectedPlayPile.add(12);
        expectedPlayPile.add(16);
        hand.add(8);
        hand.add(12);
        hand.add(16);
        hand.add(1);

        playPile = cpu.takeTurn(playPile, hand);

        assertEquals("Straights AI should play 5, 6, 7", expectedPlayPile, playPile);
    }

    @Test
    public void singlesHumanShouldPlay4() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(3);
        expectedPlayPile.add(4);
        hand.add(4);

        human.updatePlayPile(playPile);
        playPile = human.playCards(hand);

        assertEquals("Singles HostHuman should play 4", expectedPlayPile, playPile);
    }

    @Test
    public void singlesHumanShouldPlay4OfHearts() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(4);
        expectedPlayPile.add(7);
        hand.add(7);

        human.updatePlayPile(playPile);
        playPile = human.playCards(hand);

        assertEquals("Singles HostHuman should play 4", expectedPlayPile, playPile);
    }

    @Test
    public void bombSingle2() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(51);
        expectedPlayPile.add(0);
        expectedPlayPile.add(1);
        expectedPlayPile.add(4);
        expectedPlayPile.add(5);
        expectedPlayPile.add(8);
        expectedPlayPile.add(9);
        hand.add(0);
        hand.add(1);
        hand.add(2);
        hand.add(4);
        hand.add(5);
        hand.add(8);
        hand.add(9);
        hand.add(13);

        assertEquals("Bomb single 2", expectedPlayPile, cpu.takeTurn(playPile, hand));
    }

    @Test
    public void bombDouble2() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(51);
        playPile.add(50);
        expectedPlayPile.add(0);
        expectedPlayPile.add(1);
        expectedPlayPile.add(4);
        expectedPlayPile.add(5);
        expectedPlayPile.add(8);
        expectedPlayPile.add(9);
        expectedPlayPile.add(12);
        expectedPlayPile.add(13);
        hand.add(0);
        hand.add(1);
        hand.add(2);
        hand.add(4);
        hand.add(5);
        hand.add(8);
        hand.add(9);
        hand.add(12);
        hand.add(13);
        hand.add(19);

        assertEquals("Bomb double 2", expectedPlayPile, cpu.takeTurn(playPile, hand));
    }

    @Test
    public void bombTriple2() {
        ArrayList<Integer> playPile = new ArrayList<Integer>();
        ArrayList<Integer> expectedPlayPile = new ArrayList<Integer>();
        ArrayList<Integer> hand = new ArrayList<Integer>();
        playPile.add(51);
        playPile.add(50);
        playPile.add(49);
        expectedPlayPile.add(0);
        expectedPlayPile.add(1);
        expectedPlayPile.add(4);
        expectedPlayPile.add(5);
        expectedPlayPile.add(8);
        expectedPlayPile.add(9);
        expectedPlayPile.add(12);
        expectedPlayPile.add(13);
        expectedPlayPile.add(16);
        expectedPlayPile.add(17);
        hand.add(0);
        hand.add(1);
        hand.add(2);
        hand.add(4);
        hand.add(5);
        hand.add(8);
        hand.add(9);
        hand.add(12);
        hand.add(13);
        hand.add(19);
        hand.add(16);
        hand.add(17);
        hand.add(13);

        assertEquals("Bomb triple 2", expectedPlayPile, cpu.takeTurn(playPile, hand));
    }
}