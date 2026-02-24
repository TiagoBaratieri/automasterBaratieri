-- 1. TABELAS SEM DEPENDÊNCIAS
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado) VALUES ('João da Silva', '11122233344', '44999999999', 'joao@email.com', 'Rua das Flores', '123', 'Centro', '87111000', 'Sarandi', 'PR');
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado) VALUES ('Auto Peças Avenida', '12345678000199', '4433334444', 'contato@avenida.com', 'Av. Maringá', '1000', 'Jardim', '87111222', 'Sarandi', 'PR');

-- Inserindo Mecânicos
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Carlos Motor', '364.916.960-65', 'Mecânica Geral', 10.00, true);
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Beto Suspensão', '363.465.300-03', 'Suspensão e Freios', 15.00, true);

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

-- =======================================================
-- MODELO 3: MANUTENÇÃO AUTOMOTIVA
-- =======================================================

-- 1. Cliente, Mecânico, Serviço e Peça
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado) VALUES ('Thiago Baratieri', '00011122233', '44988887777', 'thiago@email.com', 'Rua das Acácias', '456', 'Jardim Alvorada', '87111333', 'Sarandi', 'PR');
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Marcos Injeção', '123.456.789-00', 'Injeção Eletrônica', 20.00, true);
INSERT INTO servico (descricao, valor_mao_de_obra_base) VALUES ('Limpeza e Equalização de Bicos', 150.00);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version) VALUES ('SKU-003', 'Jogo de Velas de Ignição', 'SP-4040', 'NGK', 'GM 1.8 8V', 60.00, 120.00, 15, 4, 0);

-- 2. Veículo (ID 3)
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id) VALUES ('MER2004', 'Meriva 1.8', 'Chevrolet', 2004, 3);

-- 3. Ordem de Serviço (ID 2 - STATUS CORRIGIDO)
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (3, 'EM_EXECUCAO', CURRENT_TIMESTAMP, 'Veículo falhando em baixa rotação e consumindo muito combustível', 0.00);

-- 4. Itens da OS (Amarrados a OS 2)
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (2, 3, 1, 120.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (2, 3, 3, 1, 150.00, 'Limpeza feita na máquina de ultrassom');


-- =======================================================
-- MODELO 4: MANUTENÇÃO DUAS RODAS
-- =======================================================

-- 1. Mecânico, Serviço e Peça (Aproveitando o cliente 3)
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Alex Duas Rodas', '987.654.321-11', 'Mecânica de Motocicletas', 25.00, true);
INSERT INTO servico (descricao, valor_mao_de_obra_base) VALUES ('Troca de Kit Relação', 80.00);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version) VALUES ('SKU-004', 'Kit Relação com Retentor', 'KR-5050', 'DID', 'Yamaha 250cc', 180.00, 320.00, 8, 2, 0);

-- 2. Veículo (ID 4)
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id) VALUES ('LAN0250', 'Lander 250', 'Yamaha', 2020, 3);

-- 3. Ordem de Serviço (ID 3)
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (4, 'FINALIZADO', CURRENT_TIMESTAMP, 'Corrente patinando, solicitada troca do kit relação completo', 0.00);

-- 4. Itens da OS (Amarrados a OS 3)
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (3, 4, 1, 320.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (3, 4, 4, 1, 80.00, 'Lubrificação da balança incluída na mão de obra');