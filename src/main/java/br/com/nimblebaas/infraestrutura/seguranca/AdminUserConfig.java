package br.com.nimblebaas.infraestrutura.seguranca;

import br.com.nimblebaas.domain.role.model.Role;
import br.com.nimblebaas.domain.role.repository.RoleRepository;
import br.com.nimblebaas.domain.usuario.model.Usuario;
import br.com.nimblebaas.domain.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserConfig(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByDescricao(Role.RoleValues.ADMIN.name());
        var usuarioAdmin = usuarioRepository.findById(1L);
        usuarioAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Usuario já existe");
                },
                () -> {
                    var usuario = new Usuario(null, "admin@admin.com", "61299652069", "Admnistrator", passwordEncoder.encode("senha"), Set.of(roleAdmin));
                    usuarioRepository.save(usuario);
                });
    }
}
