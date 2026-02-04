-- GIMP_DSV.dbo.CL_Checklist definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_Checklist;

CREATE TABLE GIMP_DSV.dbo.CL_Checklist (
	Id int IDENTITY(1,1) NOT NULL,
	IdRamo int NOT NULL,
	NomeDocumento varchar(50) COLLATE Latin1_General_CI_AS NOT NULL,
	CentroCusto varchar(5) COLLATE Latin1_General_CI_AS NOT NULL,
	CONSTRAINT PK_Checklist PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_ChecklistVersao definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_ChecklistVersao;

CREATE TABLE GIMP_DSV.dbo.CL_ChecklistVersao (
	Id int IDENTITY(1,1) NOT NULL,
	IdChecklist int NOT NULL,
	IdUsuario int NOT NULL,
	Status int NOT NULL,
	IdDemanda varchar(40) COLLATE Latin1_General_CI_AS NOT NULL,
	DataCadastro datetime NOT NULL,
	DataAtualizacao datetime NOT NULL,
	IsIcatu bit NOT NULL,
	IsRioGrande bit NOT NULL,
	IsCaixa bit NOT NULL,
	Versao int NOT NULL,
	CONSTRAINT PK_CL_Versao_Checklist PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_Layout definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_Layout;

CREATE TABLE GIMP_DSV.dbo.CL_Layout (
	Id int IDENTITY(1,1) NOT NULL,
	IdChecklistVersao int NOT NULL,
	DataAtualizacao datetime NOT NULL,
	NomeLayout varchar(100) COLLATE Latin1_General_CI_AS NOT NULL,
	TipoMIME varchar(200) COLLATE Latin1_General_CI_AS NOT NULL,
	ConteudoLayout varbinary(MAX) NOT NULL,
	Observacao nvarchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	ViaServico bit NULL,
	ViaTxt bit NULL,
	CONSTRAINT PK_Layout PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_MassaDados definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_MassaDados;

CREATE TABLE GIMP_DSV.dbo.CL_MassaDados (
	Id int IDENTITY(1,1) NOT NULL,
	IdLayout int NOT NULL,
	DataAtualizacao datetime NOT NULL,
	NomeMassaDados varchar(100) COLLATE Latin1_General_CI_AS NOT NULL,
	TipoMIME varchar(200) COLLATE Latin1_General_CI_AS NOT NULL,
	ConteudoMassaDados varbinary(MAX) NOT NULL,
	Observacao nvarchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	CONSTRAINT PK_MassaDados PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_ModeloDocumento definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_ModeloDocumento;

CREATE TABLE GIMP_DSV.dbo.CL_ModeloDocumento (
	Id int IDENTITY(1,1) NOT NULL,
	IdChecklistVersao int NOT NULL,
	DataAtualizacao datetime NOT NULL,
	IdLogoCapa int NULL,
	IdLogoProduto int NULL,
	CodAssIH varchar(4) COLLATE Latin1_General_CI_AS NULL,
	CodAssParc varchar(5) COLLATE Latin1_General_CI_AS NULL,
	NomeRecurso varchar(100) COLLATE Latin1_General_CI_AS NOT NULL,
	TipoMIME varchar(200) COLLATE Latin1_General_CI_AS NOT NULL,
	ConteudoRecurso varbinary(MAX) NOT NULL,
	Observacao nvarchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	IsImpresso bit NOT NULL,
	IsCRC bit NOT NULL,
	Duplex bit NULL,
	Acabamento int NULL,
	IdPreImpresso int NULL,
	IsArmazenamento bit NOT NULL,
	TempoArmazenamento int NOT NULL,
	AcessoBackOffice bit NULL,
	CamposBuscaBackOffice varchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	AcessoCliente bit NULL,
	CamposBuscaCliente varchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	AcessoCorretor bit NULL,
	CamposBuscaCorretor varchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	AcessoEstipulante bit NULL,
	CamposBuscaEstipulante varchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	AcessoSubEstipulante bit NULL,
	CamposBuscaSubEstipulante varchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	CONSTRAINT PK_ModeloDocumento PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_TipoEnvio definição

-- Drop table

-- DROP TABLE GIMP_DSV.dbo.CL_TipoEnvio;

CREATE TABLE GIMP_DSV.dbo.CL_TipoEnvio (
	Id int IDENTITY(1,1) NOT NULL,
	IdChecklistVersao int NOT NULL,
	DataAtualizacao datetime NOT NULL,
	Tipo int NOT NULL,
	NomeTemplate varchar(100) COLLATE Latin1_General_CI_AS NOT NULL,
	TipoMIME varchar(200) COLLATE Latin1_General_CI_AS NOT NULL,
	ConteudoTemplate varbinary(MAX) NOT NULL,
	Observacao nvarchar(MAX) COLLATE Latin1_General_CI_AS NULL,
	CONSTRAINT PK_TipoEnvio PRIMARY KEY (Id)
);


-- GIMP_DSV.dbo.CL_Checklist chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_Checklist ADD CONSTRAINT FK_Checklist_Ramo FOREIGN KEY (IdRamo) REFERENCES GIMP_DSV.dbo.Ramo(IdRamo) ON DELETE CASCADE;


-- GIMP_DSV.dbo.CL_ChecklistVersao chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_ChecklistVersao ADD CONSTRAINT FK_CL_Checklist FOREIGN KEY (IdChecklist) REFERENCES GIMP_DSV.dbo.CL_Checklist(Id) ON DELETE CASCADE;
ALTER TABLE GIMP_DSV.dbo.CL_ChecklistVersao ADD CONSTRAINT FK_Checklist_Usuario FOREIGN KEY (IdUsuario) REFERENCES GIMP_DSV.dbo.Usuarios(Id) ON DELETE CASCADE;


-- GIMP_DSV.dbo.CL_Layout chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_Layout ADD CONSTRAINT FK_Layout_ChecklistVersao FOREIGN KEY (IdChecklistVersao) REFERENCES GIMP_DSV.dbo.CL_ChecklistVersao(Id) ON DELETE CASCADE;


-- GIMP_DSV.dbo.CL_MassaDados chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_MassaDados ADD CONSTRAINT FK_MassaDados_Layout FOREIGN KEY (IdLayout) REFERENCES GIMP_DSV.dbo.CL_Layout(Id) ON DELETE CASCADE;


-- GIMP_DSV.dbo.CL_ModeloDocumento chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_AssinatIcatu FOREIGN KEY (CodAssIH) REFERENCES GIMP_DSV.dbo.AssinatIcatu(CodAssIH) ON DELETE SET NULL;
ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_AssinatParc FOREIGN KEY (CodAssParc) REFERENCES GIMP_DSV.dbo.AssinatParc(CodAssParc) ON DELETE SET NULL;
ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_ChecklistVersao FOREIGN KEY (IdChecklistVersao) REFERENCES GIMP_DSV.dbo.CL_ChecklistVersao(Id) ON DELETE CASCADE;
ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_LogoCapa FOREIGN KEY (IdLogoCapa) REFERENCES GIMP_DSV.dbo.LogoCapa(Id) ON DELETE SET NULL;
ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_LogoProduto FOREIGN KEY (IdLogoProduto) REFERENCES GIMP_DSV.dbo.LogoProduto(Id) ON DELETE SET NULL;
ALTER TABLE GIMP_DSV.dbo.CL_ModeloDocumento ADD CONSTRAINT FK_ModeloDocumento_PreImpresso FOREIGN KEY (IdPreImpresso) REFERENCES GIMP_DSV.dbo.PreImpresso(Id) ON DELETE SET NULL;


-- GIMP_DSV.dbo.CL_TipoEnvio chaves estrangeiras

ALTER TABLE GIMP_DSV.dbo.CL_TipoEnvio ADD CONSTRAINT FK_TipoEnvio_ChecklistVersao FOREIGN KEY (IdChecklistVersao) REFERENCES GIMP_DSV.dbo.CL_ChecklistVersao(Id) ON DELETE CASCADE;