package com.loja.loja.controller;

import com.loja.loja.controller.ProdutoRepository;
import com.loja.loja.model.Produto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Página inicial
    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/produtos"; // Redireciona para a lista de produtos
    }

    // Exibir a lista de produtos
    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoRepository.findAll(); // Recupera todos os produtos
        model.addAttribute("produtos", produtos); // Adiciona a lista de produtos no modelo
        return "produtos-lista"; // Nome do template produtos-lista.html
    }

    // Adicionar produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(@RequestParam("produtoId") Integer produtoId, HttpSession session) {
        Produto produto = produtoRepository.findById(produtoId).orElse(null);
        if (produto == null) {
            return "redirect:/produtos"; // Redireciona se o produto não existir
        }

        // Recupera o carrinho da sessão ou cria um novo
        List<Produto> carrinho = (List<Produto>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }

        // Adiciona o produto ao carrinho
        carrinho.add(produto);
        session.setAttribute("carrinho", carrinho); // Atualiza o carrinho na sessão

        return "redirect:/carrinho";
    }

    // Exibir o carrinho
    @GetMapping("/carrinho")
    public String exibirCarrinho(HttpSession session, Model model) {
        List<Produto> carrinho = (List<Produto>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }
        model.addAttribute("carrinho", carrinho);
        return "carrinho"; // Nome do template carrinho.html
    }

    // Finalizar a compra
    @PostMapping("/carrinho/finalizar")
    public String finalizarCompra(HttpSession session, Model model) {
        List<Produto> carrinho = (List<Produto>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("mensagem", "Seu carrinho está vazio!");
            return "carrinho";
        }

        // Limpar o carrinho da sessão
        session.removeAttribute("carrinho");

        // Exibir mensagem de sucesso
        model.addAttribute("mensagem", "Compra finalizada com sucesso!");
        return "finalizado"; // Nome do template finalizado.html
    }
}
