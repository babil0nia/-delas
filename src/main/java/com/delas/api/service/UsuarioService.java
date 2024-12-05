package com.delas.api.service;
import com.delas.api.model.TokenRedefinicaoSenhaModel;
import com.delas.api.dto.UsuarioDTO;
import com.delas.api.model.UsuarioModel;
import com.delas.api.repository.UsuarioRepository;
import com.delas.api.repository.TokenRedefinicaoSenhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRedefinicaoSenhaRepository tokenRepository;


    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void redefinirSenha(String token, String novaSenha) {
        // Busca o token de redefinição no banco
        Optional<TokenRedefinicaoSenhaModel> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Token inválido ou não encontrado.");
        }

        // Valida se o token não está expirado
        TokenRedefinicaoSenhaModel tokenModel = tokenOpt.get();
        if (tokenModel.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado.");
        }

        // Busca o usuário associado ao token
        UsuarioModel usuario = tokenModel.getId();  // Aqui estamos assumindo que você tem um relacionamento com o usuário

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // Criptografa a nova senha
        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);

        // Salva o usuário com a nova senha
        usuarioRepository.save(usuario);

        // Remove o token após o uso
        tokenRepository.delete(tokenModel);
    }

    // Método para salvar um novo usuário
    public UsuarioModel salvarUsuario(UsuarioModel usuario) {
        // Criptografia da senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    // Método para listar todos os usuários
    public List<UsuarioModel> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Método para buscar um usuário pelo ID
    public Optional<UsuarioModel> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Método para atualizar um usuário existente
    public UsuarioModel atualizarUsuario(Long id, UsuarioModel usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setTelefone(usuarioAtualizado.getTelefone());
            usuario.setTipo(usuarioAtualizado.getTipo());
            usuario.setRua(usuarioAtualizado.getRua());
            usuario.setBairro(usuarioAtualizado.getBairro());
            usuario.setCep(usuarioAtualizado.getCep());
            // Criptografar a senha se ela foi atualizada
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            }
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    // Método para deletar um usuário pelo ID
    public boolean deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
        return true;
    }

    // Buscar usuário por email
    public UsuarioModel findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Método para criar um usuário com dados do DTO
    public UsuarioModel criarUsuario(UsuarioDTO usuarioDTO) {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setTipo(UsuarioModel.TipoUsuario.valueOf(usuarioDTO.getTipo()));
        usuario.setRua(usuarioDTO.getRua());
        usuario.setBairro(usuarioDTO.getBairro());
        usuario.setCep(usuarioDTO.getCep());
        usuario.setCpf(usuarioDTO.getCpf());

        // Criptografia da senha
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        return usuarioRepository.save(usuario);
    }

    // Método no UsuarioService para buscar por resetToken
    public Optional<TokenRedefinicaoSenhaModel> findByResetToken(String token) {
        return tokenRepository.findByToken(token);
    }

    // Método para atualizar o token no usuário
    public void atualizarResetToken(String token, String email) {
        // Busca o usuário pelo email
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email: " + email));

        // Cria um novo token de redefinição de senha
        TokenRedefinicaoSenhaModel novoToken = new TokenRedefinicaoSenhaModel();
        novoToken.setToken(token);
        novoToken.setId(usuario);
        novoToken.setDataExpiracao(LocalDateTime.now().plusHours(1)); // Exemplo de expiração do token após 1 hora

        // Salva o novo token no banco
        tokenRepository.save(novoToken);
    }

    public String obterRankingPrestador(Long prestadorId) {
        UsuarioModel prestador = usuarioRepository.findById(prestadorId)
                .orElseThrow(() -> new RuntimeException("Prestador não encontrado com o ID: " + prestadorId));

        if (prestador.getTipo() != UsuarioModel.TipoUsuario.PRESTADOR) {
            throw new RuntimeException("O usuário não é um prestador.");
        }

        return prestador.determinarRanking();
    }


    // Regras de negócio para edição de perfil
    public UsuarioModel editarPerfil(Long id, UsuarioModel usuarioAtualizado) {
        // Recuperar o usuário existente no banco de dados
        UsuarioModel usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        // Validar alterações permitidas
        if (!usuarioAtualizado.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                throw new IllegalArgumentException("E-mail já está em uso por outro usuário.");
            }
        }

        if (!usuarioAtualizado.getCpf().equals(usuarioExistente.getCpf())) {
            if (usuarioRepository.existsByCpf(usuarioAtualizado.getCpf())) {
                throw new IllegalArgumentException("CPF já está em uso por outro usuário.");
            }
        }

        // Atualizar somente os campos permitidos
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        usuarioExistente.setRua(usuarioAtualizado.getRua());
        usuarioExistente.setBairro(usuarioAtualizado.getBairro());
        usuarioExistente.setCep(usuarioAtualizado.getCep());

        // Atualizar a senha somente se fornecida
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        // Persistir alterações
        return usuarioRepository.save(usuarioExistente);
    }
}
