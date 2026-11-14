package domain;

import domain.model.Produto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProdutoTest {
    @Test
    void criaProdutoComTodosOsDados() {
        Produto produto = new Produto(1L, "Café", "Bebidas", "Café torrado");

        assertEquals(1L, produto.getId());
        assertEquals("Café", produto.getNome());
        assertEquals("Bebidas", produto.getCategoria());
        assertEquals("Café torrado", produto.getDescricao());
    }

    @Test
    void permiteCriarProdutoVazioEDefinirSeusDados() {
        Produto produto = new Produto();

        assertNull(produto.getId());
        assertNull(produto.getNome());
        assertNull(produto.getCategoria());
        assertNull(produto.getDescricao());

        produto.setId(2L);
        produto.setNome("Chá");
        produto.setCategoria("Bebidas");
        produto.setDescricao("Chá verde");

        assertEquals(2L, produto.getId());
        assertEquals("Chá", produto.getNome());
        assertEquals("Bebidas", produto.getCategoria());
        assertEquals("Chá verde", produto.getDescricao());
    }
}
