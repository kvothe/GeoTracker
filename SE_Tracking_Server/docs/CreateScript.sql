SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[geolocation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_fk] [bigint] NOT NULL,
	[timestamp] [bigint] NOT NULL,
	[longitude] [float] NOT NULL,
	[latitude] [float] NOT NULL,
	[accuracy] [float] NULL,
	[altitude] [float] NULL,
	[altitude-accuracy] [float] NULL,
	[heading] [float] NULL,
	[speed] [float] NULL,
 CONSTRAINT [PK_geolocation] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[trackingsession](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[observed] [bigint] NOT NULL,
	[observer] [bigint] NOT NULL,
	[starttime] [bigint] NOT NULL,
	[endtime] [bigint] NULL,
	[canceled_by] [bigint] NULL,
 CONSTRAINT [PK_trackingsession] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[user](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[username] [nvarchar](512) NOT NULL,
	[encryptedPassword] [varbinary](20) NOT NULL,
	[salt] [varbinary](8) NOT NULL,
	[observable] [bit] NOT NULL,
 CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[geolocation]  WITH CHECK ADD  CONSTRAINT [FK_geolocation_user] FOREIGN KEY([user_fk])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[geolocation] CHECK CONSTRAINT [FK_geolocation_user]
GO
ALTER TABLE [dbo].[trackingsession]  WITH CHECK ADD  CONSTRAINT [FK_trackingsession_user_canceled] FOREIGN KEY([canceled_by])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[trackingsession] CHECK CONSTRAINT [FK_trackingsession_user_canceled]
GO
ALTER TABLE [dbo].[trackingsession]  WITH CHECK ADD  CONSTRAINT [FK_trackingsession_user_observed] FOREIGN KEY([observed])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[trackingsession] CHECK CONSTRAINT [FK_trackingsession_user_observed]
GO
ALTER TABLE [dbo].[trackingsession]  WITH CHECK ADD  CONSTRAINT [FK_trackingsession_user_observer] FOREIGN KEY([observer])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[trackingsession] CHECK CONSTRAINT [FK_trackingsession_user_observer]
GO
