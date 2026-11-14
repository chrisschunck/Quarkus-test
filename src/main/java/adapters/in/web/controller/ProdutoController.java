package adapters.in.web.controller;

import adapters.in.web.dto.ProdutoRequestDTO;
import adapters.in.web.dto.ProdutoResponseDTO;
import application.service.ProdutoService;
import domain.model.Produto;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/produtos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoController {
    private final ProdutoService produtoService;

    @Inject
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GET
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoService.listarProdutos().stream()
                .map(ProdutoController::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public ProdutoResponseDTO buscarProdutoPorId(@PathParam("id") Long id) {
        return toResponse(produtoService.buscarProdutoPorId(id));
    }

    @GET
    @Path("/nome/{nome}")
    public ProdutoResponseDTO buscarProdutoPorNome(@PathParam("nome") String nome) {
        return toResponse(produtoService.buscarProdutoPorNome(nome));
    }

    @POST
    public Response registrarProduto(ProdutoRequestDTO request) {
        Produto produto = produtoService.registrarProduto(toDomain(request));
        return Response.status(Response.Status.CREATED).entity(toResponse(produto)).build();
    }

    @PUT
    @Path("/{id}")
    public ProdutoResponseDTO atualizarProdutoPorId(@PathParam("id") Long id, ProdutoRequestDTO request) {
        return toResponse(produtoService.atualizarProdutoPorId(id, toDomain(request)));
    }

    @PATCH
    @Path("/{id}")
    public ProdutoResponseDTO atualizarParcialmenteProduto(
            @PathParam("id") Long id,
            ProdutoRequestDTO request
    ) {
        return toResponse(produtoService.atualizarParcialmenteProduto(id, toDomain(request)));
    }

    @DELETE
    @Path("/{id}")
    public Response deletarProdutoPorId(@PathParam("id") Long id) {
        produtoService.deletarProdutoPorId(id);
        return Response.noContent().build();
    }

    private static Produto toDomain(ProdutoRequestDTO request) {
        if (request == null) {
            return null;
        }
        return new Produto(null, request.nome(), request.categoria(), request.descricao());
    }

    private static ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getDescricao()
        );
    }
}
