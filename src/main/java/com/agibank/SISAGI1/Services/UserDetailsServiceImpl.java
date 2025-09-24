package com.agibank.SISAGI1.Services;

import com.agibank.SISAGI1.Repositories.ClienteRepository;
import com.agibank.SISAGI1.Repositories.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var cliente = clienteRepository.findByEmail(email);
        if (cliente.isPresent()) {
            return cliente.get();
        }
        var gerente = gerenteRepository.findByEmail(email);
        if (gerente.isPresent()) {
            return gerente.get();
        }
        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}
