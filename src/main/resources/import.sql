-- 1. CLIENTES, MECÂNICOS E SERVIÇOS (TODOS COM 'ATIVO')
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado, ativo) VALUES ('João da Silva', '11122233344', '44999999999', 'joao@email.com', 'Rua das Flores', '123', 'Centro', '87111000', 'Sarandi', 'PR', true);
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado, ativo) VALUES ('Auto Peças Avenida', '12345678000199', '4433334444', 'contato@avenida.com', 'Av. Maringá', '1000', 'Jardim', '87111222', 'Sarandi', 'PR', true);
INSERT INTO cliente (nome, cpf_ou_cnpj, telefone, email, logradouro, numero, bairro, cep, cidade, estado, ativo) VALUES ('Thiago Baratieri', '00011122233', '44988887777', 'thiago@email.com', 'Rua das Acácias', '456', 'Jardim Alvorada', '87111333', 'Sarandi', 'PR', true);

INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Carlos Motor', '364.916.960-65', 'Mecânica Geral', 10.00, true);
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Beto Suspensão', '363.465.300-03', 'Suspensão e Freios', 15.00, true);
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Roberto', '11122233344', 'Suspensão', 15.00, true);
INSERT INTO mecanico (nome, cpf, especialidade, taxa_comissao, ativo) VALUES ('Alex Duas Rodas', '987.654.321-11', 'Mecânica de Motocicletas', 25.00, true);

INSERT INTO servico (descricao, valor_mao_de_obra_base, ativo) VALUES ('Troca de Óleo + Filtros', 60.00, true);
INSERT INTO servico (descricao, valor_mao_de_obra_base, ativo) VALUES ('Alinhamento e Balanceamento', 120.00, true);
INSERT INTO servico (descricao, valor_mao_de_obra_base, ativo) VALUES ('Limpeza e Equalização de Bicos', 150.00, true);
INSERT INTO servico (descricao, valor_mao_de_obra_base, ativo) VALUES ('Troca de Kit Relação', 80.00, true);

-- 2. PEÇAS (AGORA COM 'ATIVO')
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version, ativo) VALUES ('SKU-001', 'Filtro de Óleo', 'PN-1010', 'Fram', 'Universal', 20.00, 45.00, 50, 5, 0, true);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version, ativo) VALUES ('SKU-002', 'Pastilha de Freio', 'PN-2020', 'Cobreq', 'Civic/Corolla', 80.00, 150.00, 10, 4, 0, true);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version, ativo) VALUES ('SKU-003', 'Jogo de Velas de Ignição', 'SP-4040', 'NGK', 'GM 1.8 8V', 60.00, 120.00, 15, 4, 0, true);
INSERT INTO peca (sku, nome, part_number, marca, aplicacao, preco_custo, preco_venda, quantidade_estoque, estoque_minimo, version, ativo) VALUES ('SKU-004', 'Kit Relação com Retentor', 'KR-5050', 'DID', 'Yamaha 250cc', 180.00, 320.00, 8, 2, 0, true);

-- 3. VEÍCULOS (SEM ATIVO, POIS NÃO FOI ESTENDIDO NESSA CLASSE)
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id,ativo) VALUES ('ABC1234', 'Civic', 'Honda', 2020, 1,true);
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id,ativo) VALUES ('XYZ9876', 'Hilux', 'Toyota', 2018, 2,true);
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id,ativo) VALUES ('MER2004', 'Meriva 1.8', 'Chevrolet', 2004, 3,true);
INSERT INTO veiculo (placa, modelo, marca, ano, cliente_id,ativo) VALUES ('LAN0250', 'Lander 250', 'Yamaha', 2020, 3,true);

-- 4. ORDENS DE SERVIÇO E ITENS
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (1, 'AGUARDANDO_APROVACAO', CURRENT_TIMESTAMP, 'Cliente relatou barulho ao frear e pediu troca de óleo', 0.00);
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (3, 'EM_EXECUCAO', CURRENT_TIMESTAMP, 'Veículo falhando em baixa rotação e consumindo muito combustível', 0.00);
INSERT INTO ordem_servico (veiculo_id, status, data_abertura, descricao, valor_total) VALUES (4, 'FINALIZADO', CURRENT_TIMESTAMP, 'Corrente patinando, solicitada troca do kit relação completo', 0.00);

-- ITENS DA OS 1
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (1, 1, 1, 45.00);
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (1, 2, 1, 150.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (1, 1, 1, 1, 60.00, 'Óleo trazido pelo cliente, cobrado apenas mão de obra e filtro');

-- ITENS DA OS 2
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (2, 3, 1, 120.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (2, 3, 3, 1, 150.00, 'Limpeza feita na máquina de ultrassom');

-- ITENS DA OS 3
INSERT INTO item_peca (ordem_servico_id, peca_id, quantidade, preco_unitario) VALUES (3, 4, 1, 320.00);
INSERT INTO item_servico (ordem_servico_id, servico_id, mecanico_id, quantidade, valor_cobrado, observacao) VALUES (3, 4, 4, 1, 80.00, 'Lubrificação da balança incluída na mão de obra');