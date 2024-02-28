package com.cibertec.assessment.service.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.assessment.beans.PolygonBean;

import com.cibertec.assessment.model.Polygon;
import com.cibertec.assessment.model.Square;
import com.cibertec.assessment.repo.SquareRepo;
import com.cibertec.assessment.service.PolygonService;
import com.cibertec.assessment.service.SquareService;

@Service
public class SquareServiceImpl implements SquareService {

    private final SquareRepo squareRepo;
    private final PolygonService polygonService;

    @Autowired
    public SquareServiceImpl(SquareRepo squareRepo, PolygonService polygonService) {
        this.squareRepo = squareRepo;
        this.polygonService = polygonService;
    }

    @Override
    public Square create(Square s) {
    	// los puntos x e y no sean nulos , ni que esten en blanco   	
        s.setX_points(s.getX_points() == null || s.getX_points().isBlank() ? "[]" : s.getX_points());
        s.setY_points(s.getY_points() == null || s.getY_points().isBlank() ? "[]" : s.getY_points());

        //se crea una lista para almacenar IDs de los poligonos que se interceptan con el cuadrado
        List<String> overlappingPolygonIds = new ArrayList<>();

        //las coordenadas del cuadrado se convierten de cadena a array de enteros
        Integer[] xPointsSquare = convertStringToIntArray(s.getX_points());
        Integer[] yPointsSquare = convertStringToIntArray(s.getY_points());
       

        //se obtienen todos los poligonos a traves del servicio poligonservice
        //y se convierten de polygonBean a polygon
        List<Polygon> allPolygons = polygonService.list().stream()
                .map(this::convertPolygonBeanToPolygon)
                .collect(Collectors.toList());

        //Para cada polígono, se verifica si hay superposición con el cuadrado.
        for (Polygon p : allPolygons) {
            if (checkOverlap(p, xPointsSquare, yPointsSquare)) {
            	//Si hay una superposición, se agrega el ID del polígono a la lista overlappingPolygonIds.
                overlappingPolygonIds.add(p.getId().toString());
            }
        }

        //se convierte el list de poligonos en cadena
        String polygonsArrayString = overlappingPolygonIds.toString();
        //Se establece la cadena de texto de IDs de polígonos en el campo polygons del cuadrado (s).
        s.setPolygons(polygonsArrayString);

        //se guarda el cuadrado en la BD
        return squareRepo.save(s);
    }

    @Override
    public List<Square> list() {
        return squareRepo.findAll();
    }

    //Convertir el objeto PolygonBean a Polygon
    private Polygon convertPolygonBeanToPolygon(PolygonBean polygonBean) {
        return new Polygon(polygonBean.getId(), polygonBean.getName(), polygonBean.getNpoints(),
        		//Convierte el arreglo de coordenadas  del polígono a una cadena de texto.
                Arrays.toString(polygonBean.getXPoints()), Arrays.toString(polygonBean.getYPoints()));
    }


    //verificar si al menos hay algun punto dentro del poligono
    private boolean checkOverlap(Polygon p, Integer[] xPointsSquare, Integer[] yPointsSquare) {
      //iteracion sobre los puntos del cuadrado
        for (int i = 0; i < xPointsSquare.length; i++) {
            if (isPointInsidePolygon(p, xPointsSquare[i], yPointsSquare[i])) {
                //si encuentra al menos un punto del cuadrado devuelve true
            	return true;
            }
        }
        return false;
    }
    //Determinar si un punto esta adentro
    private boolean isPointInsidePolygon(Polygon polygon, int x, int y) {
    	//Convierte las cadenas de coordenadas del poligono en arreglos de enteros
    	Integer[] xPoints = convertStringToIntArray(polygon.getXPoints());
        Integer[] yPoints = convertStringToIntArray(polygon.getYPoints());

        boolean inside = false;
        //iteracion sobre los puntos del poligono
        //calcula si el punto del cuadrado se intercepta con el poligono utilizando el algorito ray casting
        for (int i = 0, j = xPoints.length - 1; i < xPoints.length; j = i++) {
            if ((yPoints[i] > y) != (yPoints[j] > y) &&
                (x < (xPoints[j] - xPoints[i]) * (y - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i])) {
                inside = !inside;
            }
        }
        //devuelve que el punto si intercepta el poligono
        return inside;
    }
    private Integer[] convertStringToIntArray(String arrayAsString) {
        try {
           //elimanr los corchetes de inicio y fin de la cadena
            arrayAsString = arrayAsString.replaceAll("\\[", "").replaceAll("\\]", "").trim();
            
            if (arrayAsString.isEmpty()) {
            	//me devuelve un arreglo de enteros vacio
                return new Integer[0];
            }
            String[] items = arrayAsString.split(",");
            return Arrays.stream(items)
                    .map(String::trim)
                    //se filtran los elemnetos vacios
                    .filter(item -> !item.isEmpty())
                    //se convierte los elemnetos de la cadena en enteros
                    .map(Integer::valueOf)
                    //convierte el flujo de enteros en un arreglo de objetos INTEGER
                    .toArray(Integer[]::new);
        } catch (NumberFormatException e) {
            // Manejar la excepción, por ejemplo, registrándola y devolviendo null o un array vacío
            e.printStackTrace();
            //DEVUELVE UN NUEVO ARREGLO VACIO
            return new Integer[0];
        }
    }

	@Override
	public List<Square> findSquaresByName(String name) {
		return squareRepo.findByName(name);
	}

}