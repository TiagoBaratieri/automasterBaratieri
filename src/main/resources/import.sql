-- 1. TABELAS SEM DEPENDÊNCIAS
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado) VALUES ('João da Silva', '11122233344', '44999999999', 'joao@email.com', 'Rua das Flores', '123', 'Centro', '87111000', 'Sarandi', 'PR');
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado) VALUES ('Auto Peças Avenida', '12345678000199', '4433334444', 'contato@avenida.com', 'Av. Maringá', '1000', 'Jardim', '87111222', 'Sarandi', 'PR');

-- Inserindo Mecânicos
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Carlos Motor', '55566677788', 'Mecânica Geral', 10.00, true);
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Beto Suspensão', '99988877766', 'Suspensão e Freios', 15.00, true);

-- Inserindo Serviços Base
INSERT INTO servico (descricao, valor_mao_de_obra_base) VALUES ('Troca de Óleo + Filtros', 60.00);
INSERT INTO servico (descricao, valor_mao_de_obra_base) VALUES ('Alinhamento e Balanceamento', 120.00);

-- Inserindo Peças
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version) VALUES ('SKU-001', 'Filtro de Óleo', 'PN-1010', 'Fram', 'Universal', 20.00, 45.00, 50, 5, 0);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version) VALUES ('SKU-002', 'Pastilha de Freio', 'PN-2020', 'Cobreq', 'Civic/Corolla', 80.00, 150.00, 10, 4, 0);

-- 2. TABELAS COM 1 DEPENDÊNCIA
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id) VALUES ('ABC1234', 'Civic', 'Honda', 2020, 1);
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id) VALUES ('XYZ9876', 'Hilux', 'Toyota', 2018, 2);

-- 3. A ORDEM DE SERVIÇO
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (1, 'ORCAMENTO', CURRENT_TIMESTAMP, 'Cliente relatou barulho ao frear e pediu troca de óleo', 0.00);

-- 4. ITENS DA OS
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (1, 1, 1, 45.00);
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (1, 2, 1, 150.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (1, 1, 1, 1, 60.00, 'Óleo trazido pelo cliente, cobrado apenas mão de obra e filtro');