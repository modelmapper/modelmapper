package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH565 extends AbstractTest {

  public void shouldMap() {
    DBBoardNode dbNode1 = new DBBoardNode();
    DBBoardNode dbNode2 = new DBBoardNode();
    DBChessBoard dbChessBoard = new DBChessBoard();
    dbChessBoard.nodes = Arrays.asList(dbNode1, dbNode2);
    DBPlayer source = new DBPlayer();
    source.favoriteTile = dbNode2;
    source.favoritePiece = new DBChessPiece();
    source.favoritePiece.node = dbNode1;
    dbNode1.pieces = Collections.singletonList(source.favoritePiece);
    dbNode1.board = dbChessBoard;
    dbNode2.board = dbChessBoard;

    modelMapper.getConfiguration().setPreferNestedProperties(false);
    Player target = modelMapper.map(source, Player.class);

    assertEquals(target.favoriteTile.board.nodes.size(), 2);
    BoardNode node1 = target.favoriteTile.board.nodes.get(0);
    BoardNode node2 = target.favoriteTile.board.nodes.get(1);
    assertSame(target.favoriteTile, node2);
    assertSame(target.favoritePiece.node, node1);
    assertSame(node1.board, node2.board);
    assertSame(node1.pieces.get(0), target.favoritePiece);
  }

  private static class DBPlayer {
    DBBoardNode favoriteTile;
    DBChessPiece favoritePiece;
  }

  private static class DBBoardNode {
    List<DBChessPiece> pieces;
    DBChessBoard board;
    int x;
    int y;
  }

  private static class DBChessPiece {
    String id;
    DBBoardNode node;
  }

  private static class DBChessBoard {
    List<DBBoardNode> nodes;
  }

  private static class Player {
    BoardNode favoriteTile;
    ChessPiece favoritePiece;
  }

  private static class BoardNode {
    List<ChessPiece> pieces;
    ChessBoard board;
    int x;
    int y;
  }

  private static class ChessPiece {
    String id;
    BoardNode node;
  }

  private static class ChessBoard {
    List<BoardNode> nodes;
  }
}
