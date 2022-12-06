package com.example;


import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.websocket.*;
import javax.json.bind.JsonbBuilder;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * explicacao detalhada: https://www.devmedia.com.br/java-websockets-introducao/30443
 */

@ServerEndpoint(value = "/chat") //decora a classe como um WebSocket-Endpoint disponibilizado no URI mencionado no value da anotação!
@ApplicationScoped
public class ChatController {

    Set<Session> sessoes = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen // Quando uma conexão for aberta, o método com esta anotação será chamado!
    public void abrir(Session sessaoAbertura) {
        System.out.println("Nova solicitação recebida. Id: " + sessaoAbertura.getId());
        sessoes.add(sessaoAbertura);
    }

    @OnClose // Quando uma conexão for encerrada, o método com esta anotação será chamado!
    public void fechar(Session sessaoEncerrramento) {
        System.out.println("Conexão encerrada. Id: " + sessaoEncerrramento.getId());
        sessoes.remove(sessaoEncerrramento);
    }

    @OnError // Quando houver erro de conexao, o método com esta anotação será chamado!
    public void erro(Throwable t) {
        System.out.println("Ocorreu um erro de conexao: ");
        t.printStackTrace();
    }

    @OnMessage // Recebe uma mensagem de entrada do WebSocket
    public void recebeMensagem(String mensagemRecebida) {
        String mensagem = obterMensagemTratada(mensagemRecebida);
        enviar(mensagem);
    }

    private void enviar(String mensagem) {
        sessoes.forEach(sessao -> {
            sessao.getAsyncRemote().sendObject(mensagem, resultadoDeEnvio -> {
                if (resultadoDeEnvio.getException() != null) {
                    System.out.println("Não foi possível enviar a mensagem: " + resultadoDeEnvio.getException());
                }
            });
        });
    }

    private String obterMensagemTratada(String mensagem) {
        Jsonb jsonb = JsonbBuilder.create();
        Mensagem mensagem2 = jsonb.fromJson(mensagem, Mensagem.class);
        mensagem2.message = mensagem2.user + ">> " + mensagem2.message;
        return jsonb.toJson(mensagem2);
    }
}
