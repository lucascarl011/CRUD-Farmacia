package com.generation.pharmacia.controller;

import com.generation.pharmacia.model.Produto;
import com.generation.pharmacia.repository.CategoriaRepository;
import com.generation.pharmacia.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<Produto>> getAll() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getById(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/validade/{validade}")
    public ResponseEntity<List<Produto>> getNome(@PathVariable String validade) {
        return ResponseEntity.ok(produtoRepository.findAllByNomeContainingIgnoreCase(validade));
    }

    @PostMapping
    public ResponseEntity<Produto> post(@Valid @RequestBody Produto produto) {
        if (categoriaRepository.existsById(produto.getCategoria().getId()))
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(produtoRepository.save(produto));

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não existe!", null);
    }

    @PutMapping
    public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto){
        if(produtoRepository.existsById(produto.getId())) {

            if(categoriaRepository.existsById(produto.getCategoria().getId()))
                return ResponseEntity.status(HttpStatus.OK)
                        .body(produtoRepository.save(produto));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não existe!", null);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Produto> doacao = produtoRepository.findById(id);

        if(doacao.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        produtoRepository.deleteById(id);
    }
}
