USE [master]
GO
/****** Object:  Database [Fashion_Shop2]    Script Date: 5/18/2022 2:29:02 PM ******/
CREATE DATABASE [Fashion_Shop2]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'Fashion_Shop2', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER\MSSQL\DATA\Fashion_Shop2.mdf' , SIZE = 4096KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'Fashion_Shop2_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER\MSSQL\DATA\Fashion_Shop2_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [Fashion_Shop2] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [Fashion_Shop2].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [Fashion_Shop2] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET ARITHABORT OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [Fashion_Shop2] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [Fashion_Shop2] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET  DISABLE_BROKER 
GO
ALTER DATABASE [Fashion_Shop2] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [Fashion_Shop2] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET RECOVERY FULL 
GO
ALTER DATABASE [Fashion_Shop2] SET  MULTI_USER 
GO
ALTER DATABASE [Fashion_Shop2] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [Fashion_Shop2] SET DB_CHAINING OFF 
GO
ALTER DATABASE [Fashion_Shop2] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [Fashion_Shop2] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [Fashion_Shop2] SET DELAYED_DURABILITY = DISABLED 
GO
EXEC sys.sp_db_vardecimal_storage_format N'Fashion_Shop2', N'ON'
GO
USE [Fashion_Shop2]
GO
/****** Object:  Table [dbo].[Account]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Account](
	[Username] [varchar](256) NOT NULL,
	[Password] [varchar](256) NOT NULL,
	[Fullname] [nvarchar](256) NOT NULL,
	[Role] [int] NOT NULL,
	[Email] [varchar](50) NULL,
	[Gender] [bit] NOT NULL,
	[Image] [varchar](500) NULL,
	[Birthday] [date] NULL,
	[Phone] [char](10) NOT NULL,
	[Address] [nvarchar](256) NULL,
 CONSTRAINT [PK_Account] PRIMARY KEY CLUSTERED 
(
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Color]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Color](
	[Colorname] [nvarchar](50) NOT NULL,
	[idProduct] [nvarchar](50) NULL,
 CONSTRAINT [PK_Color] PRIMARY KEY CLUSTERED 
(
	[Colorname] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Order]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Order](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Cus_name] [nvarchar](256) NOT NULL,
	[Phone] [char](10) NULL,
	[Cus_address] [nvarchar](256) NULL,
	[Cus_email] [varchar](256) NULL,
	[Date] [date] NOT NULL,
	[TotalPrice] [bigint] NOT NULL,
 CONSTRAINT [PK_Order] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OrderDetail]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrderDetail](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ID_order] [int] NOT NULL,
	[ID_product] [nvarchar](50) NOT NULL,
	[Quantity] [int] NOT NULL,
	[Price] [float] NULL,
 CONSTRAINT [PK_OrderDetail] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Product]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Product](
	[ID] [nvarchar](50) NOT NULL,
	[Name] [nvarchar](256) NOT NULL,
	[IDCategory] [nvarchar](50) NOT NULL,
	[Price] [float] NOT NULL,
	[Color] [nvarchar](50) NULL,
	[Size] [nvarchar](50) NULL,
	[Quantity] [int] NULL,
	[Image] [varchar](500) NULL,
 CONSTRAINT [PK_Product] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProductCategory]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProductCategory](
	[ID] [nvarchar](50) NOT NULL,
	[Name] [nvarchar](256) NOT NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Role]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Role](
	[ID] [int] NOT NULL,
	[Name] [nvarchar](256) NULL,
 CONSTRAINT [PK_Role] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Size]    Script Date: 5/18/2022 2:29:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Size](
	[nameSize] [nvarchar](50) NOT NULL,
	[idProduct] [nvarchar](50) NULL,
 CONSTRAINT [PK_Size] PRIMARY KEY CLUSTERED 
(
	[nameSize] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
USE [master]
GO
ALTER DATABASE [Fashion_Shop2] SET  READ_WRITE 
GO
