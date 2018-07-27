package core;

import java.sql.Connection;

import bean.ControlPage.SectionPage;
import bean.User;

public class ControlPageCore {
	public static String renderMenu(Connection conn, User usuari, String seccio) {
		StringBuilder menu = new StringBuilder();
		String active = "";
		String collapse = "";
		//Control
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.control)) {			
			if (seccio.equals("Control")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='control' class='loadingButton'  data-msg='Carregant control...'><i class='fa fa-fw fa-tasks'></i> Control</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Tasques
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.tasques_list)) {			
			if (seccio.equals("Tasques")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='tascaList' class='loadingButton'  data-msg='Carregant tasques...'><i class='fa fa-fw fa-tasks'></i> Tasques</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Registre
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.registre_ent_list) || UsuariCore.hasPermision(conn, usuari, SectionPage.registre_sort_list)) {
			if ( seccio.equals("Registre")) {
				active = "active"; 
				collapse = "in";
			}		
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#registres'><i class='fa fa-fw fa-archive'></i> Registre <i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='registres' class='nav nav-second-level collapse " + collapse + "'>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.registre_ent_list)) {
				menu.append("   	<li>");
				menu.append("   	    <a href='registreEntrada' class='loadingButton'  data-msg='Carregant registre entrada...'>Entrades</a>");
				menu.append("   	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.registre_sort_list)) {
				menu.append("   	<li>");
				menu.append("       	<a href='registreSortida' class='loadingButton'  data-msg='Carregant registre sortida...'>Sortides</a>");
				menu.append("   	</li>");
			}
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Incidencies
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.incidencia_list)) {			
			if (seccio.equals("Incidencies")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='incidencies' class='loadingButton'  data-msg='Carregant incidències...'><i class='fa fa-fw fa-bookmark-o'></i> Incidències</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Actuacions
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_list)) {			
			if (seccio.equals("Actuacions")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='actuacions' class='loadingButton'  data-msg='Carregant actuacions...'><i class='fa fa-fw fa-bookmark'></i> Actuacions</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Projectes
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.projectes_list)) {			
			if (seccio.equals("projectes")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='projectes' class='loadingButton'  data-msg='Carregant projectes...'><i class='fa fa-fw fa-bookmark'></i> Projectes</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Actuacions Manuals
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.actuacio_manual)) {			
			if (seccio.equals("ActuacionsManuals")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='createCompletActuacio' class='loadingButton'  data-msg='Carregant formulari...'><i class='fa fa-fw fa-bookmark'></i> Crear actuació manual</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Personal
		if (true) {	
			if (seccio.equals("personal")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#personalMenu'><i class='fa fa-fw fa-suitcase'></i> Personal<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='personalMenu' class='nav nav-second-level collapse " + collapse + "'>");
			menu.append("		<li>");
			menu.append("       	<a href='CalendariCotxe' class='loadingButton'  data-msg='Carregant vehicles...'>Reserva Vehicles</a>");
			menu.append("    	</li>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.personal)) {
				menu.append("		<li>");
				menu.append("       	<a href='vacancesList' class='loadingButton'  data-msg='Carregant vacances...'>Vacances</a>");
				menu.append("    	</li>");
			}			
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.personal)) {
				menu.append("		<li>");
				menu.append("       	<a href='usuarisList' class='loadingButton'  data-msg='Carregant usuaris...'>Usuaris</a>");
				menu.append("    	</li>");
			}			
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Expedients
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.expedient_list)) {	
			if (seccio.equals("expedients")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#expedientMenu'><i class='fa fa-fw fa-suitcase'></i> Expedients<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='expedientMenu' class='nav nav-second-level collapse " + collapse + "'>");
			
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.expedient_list)) {
				menu.append("		<li>");
				menu.append("			<a href='javascript:;' data-toggle='collapse' data-target='#expedientSubMenu'><i class='fa fa-fw fa-suitcase'></i> Licitacions<i class='fa fa-fw fa-caret-down'></i></a>");
				menu.append("			<ul id='expedientSubMenu' class='nav nav-third-level collapse " + collapse + "'>");
				
				menu.append("				<li>");
				menu.append("       			<a href='expedients' class='loadingButton'  data-msg='Carregant expedients...'>Expedients</a>");
				menu.append("    			</li>");
						
			
				menu.append("				<li>");
				menu.append("       			<a href='modificats' class='loadingButton'  data-msg='Carregant modificats...'>Modificats</a>");
				menu.append("    			</li>");
			
				menu.append("			</ul>");
				menu.append("    	</li>");			
			}			
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.expedient_list)) {
				menu.append("		<li>");
				menu.append("       	<a href='convenis' class='loadingButton'  data-msg='Carregant convenis...'>Convenis</a>");
				menu.append("    	</li>");
			}			
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Judicials
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.judicials_list)) {			
			if (seccio.equals("judicials")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='judicials' class='loadingButton'  data-msg='Carregant estat contenciós...'><i class='fa fa-fw fa-list'></i> Procediments judicials</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Llicencies
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.llicencia_list)) {			
			if (seccio.equals("llicencies")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='llicencies' class='loadingButton'  data-msg='Carregant autoritzacions urbanístiques...'><i class='fa fa-fw fa-list'></i> Aut Urbanístiques</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Centres
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.centres_list)) {				
			if (seccio.equals("centres")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#centresMenu'><i class='fa fa-fw fa-university'></i> Centres<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='centresMenu' class='nav nav-second-level collapse " + collapse + "'>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.centres_list)) {
				menu.append("		<li>");
				menu.append("       	<a href='centres' class='loadingButton'  data-msg='Carregant centres...'>Centres</a>");
				menu.append("    	</li>");
			}			
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.centres_crear)) {
				menu.append("		<li>");
				menu.append("       	<a href='crearCentre' class='loadingButton'  data-msg='Carregant formulari...'>Crear centre</a>");
				menu.append("    	</li>");
			}			
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}		
		//Empreses
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_list) || UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_crear)) {
			if (seccio.equals("Empreses")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#empresaMenu'><i class='fa fa-fw fa-suitcase'></i> Empreses<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='empresaMenu' class='nav nav-second-level collapse " + collapse + "'>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_list)) {
				menu.append("		<li>");
				menu.append("       	<a href='empresaList' class='loadingButton'  data-msg='Carregant empreses...'>Empreses</a>");
				menu.append("    	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_list)) {
				menu.append("		<li>");
				menu.append("       	<a href='empresaDespesaList' class='loadingButton'  data-msg='Carregant empreses...'>Empreses Despesa</a>");
				menu.append("    	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_crear)) {
				menu.append("     	<li>");
				menu.append("        	<a href='createEmpresa' class='loadingButton'  data-msg='Carregant formulari...'>Afegir empresa</a>");
				menu.append("    	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.empreses_crear)) {
				menu.append("     	<li>");
				menu.append("        	<a href='createUTE' class='loadingButton'  data-msg='Carregant formulari...'>Afegir UTE</a>");
				menu.append("    	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.bastanteos_list)) {
				menu.append("		<li class='" + active + "'>");
				menu.append("			<a href='bastanteos' class='loadingButton'  data-msg='Carregant validacions...'>Validacions</a>");
				menu.append("		</li>");
			}			
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Factures
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.factures_list)) {	
			if (seccio.equals("Factures")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#facturacioMenu'><i class='fa fa-fw fa-edit'></i> Facturació<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='facturacioMenu' class='nav nav-second-level collapse " + collapse + "'>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.factures_list)) {
				menu.append("		<li>");
				menu.append("       	<a href='factures' class='loadingButton'  data-msg='Carregant factures...'>Factures</a>");
				menu.append("    	</li>");			
				menu.append("     	<li>");
				menu.append("        	<a href='certificacions' class='loadingButton'  data-msg='Carregant certificacions...'>Certificacions</a>");
				menu.append("    	</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.factures_crear)) {
				menu.append("     	<li>");
				menu.append("        	<a href='importarFactures' class='loadingButton'  data-msg='Carregant formulari...'>Importar factures</a>");
				menu.append("    	</li>");
				menu.append("     	<li>");
				menu.append("        	<a href='facturesConformadesPend' class='loadingButton'  data-msg='Carregant factures...'>Factures per pagar noves</a>");
				menu.append("    	</li>");
			}
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
			
		}	
		//Partides
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.partides_list) || UsuariCore.hasPermision(conn, usuari, SectionPage.partides_crear)) {
			if (seccio.equals("Partides")) {
				active = "active"; 
				collapse = "in";
			}
			menu.append("<li class='"+ active + "'>");
			menu.append("	<a href='javascript:;' data-toggle='collapse' data-target='#creditMenu'><i class='fa fa-fw fa-calculator'></i> Crèdit / Partides<i class='fa fa-fw fa-caret-down'></i></a>");
			menu.append("	<ul id='creditMenu' class='nav nav-second-level collapse " + collapse + "'>");
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.partides_list)) {
				menu.append("		<li>");
				menu.append("			<a href='despeses' class='loadingButton'  data-msg='Carregant despeses...'>Consulta despeses</a>");
				menu.append("		</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.partides_list)) {
				menu.append("		<li>");
				menu.append("			<a href='credit' class='loadingButton'  data-msg='Carregant partides...'>Partides creades</a>");
				menu.append("		</li>");
			}
			if (UsuariCore.hasPermision(conn, usuari, SectionPage.partides_crear)) {
				menu.append("		<li>");
				menu.append("			<a href='CreatePartida' class='loadingButton'  data-msg='Carregant formulari...'>Crear partida</a>");
				menu.append("		</li>");
			}
			menu.append("	</ul>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}
		//Llistats
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.llistats_list)) {			
			if (seccio.equals("Llistats")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='llistats' class='loadingButton'  data-msg='Carregant mapa...'><i class='fa fa-fw fa-edit'></i> Mapa</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}	
		//Documents
		if (UsuariCore.hasPermision(conn, usuari, SectionPage.manuals)) {			
			if (seccio.equals("Manuals")) active = "active"; 
			menu.append("<li class='" + active + "'>");
			menu.append("	<a href='manuals'  class='loadingButton'  data-msg='Carregant manuals...'><i class='fa fa-fw fa-edit'></i> Manuals</a>");
			menu.append("</li>");
			active = "";
			collapse = "";
		}	
		return menu.toString();
	}
	
}
