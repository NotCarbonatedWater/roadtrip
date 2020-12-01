import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    // -------------------------------------------------------------------------------------------------------
    private class Edge {
        int vertex;
        float dist;
    }

    private class Vertex {
        public String cityName;
        public ArrayList<String> attractions; 

        public Vertex(String point) {
            cityName = point;
            attractions = new ArrayList<String>();
        }
    }

    private ArrayList<Vertex> vertices; // points
    private ArrayList<ArrayList<Edge>> edges;
    private LinkedList<Integer> places; // stops
    
    // default
    Graph() {
        vertices = new ArrayList<Vertex>();
        places = new LinkedList<Integer>();
        edges = new ArrayList<ArrayList<Edge>>();
    }

    // --------------------------------------------------------------------------------------------------------------------------
    // checks for existance //
    public boolean containsVert(String point) {
        for (int index = 0; index < vertices.size(); index++)
            if (vertices.get(index).cityName.equalsIgnoreCase(point))
                return true;
        return false; // not found
    }

    // fetches index num //
    public int getIndex(String point) {
        for (int index = 0; index < vertices.size(); index++)
            if (vertices.get(index).cityName.equalsIgnoreCase(point))
                return index;
        return -1; // not found
    }

    // adds edges and calculates distance //
    public void addEdge(String pointA, String pointB, float doMath) {
        if (containsVert(pointA) == false) { // if doessn't exist, then add it
            Vertex vert1 = new Vertex(pointA);
            vertices.add(vert1);
            edges.add(new ArrayList<Edge>());
        }
        int vertex1 = getIndex(pointA);
        ArrayList<Edge> edges1 = edges.get(vertex1);

        if (containsVert(pointB) == false) { // B-> A // edges are bi directional
            Vertex vert2 = new Vertex(pointB);
            vertices.add(vert2);
            edges.add(new ArrayList<Edge>());
        }
        int vertex2 = getIndex(pointB);
        ArrayList<Edge> edges2 = edges.get(vertex2);

        // 2 new edges // B-> A // edges are bi directional
        Edge tmpEdgeA = new Edge();
        Edge tmpEdge2 = new Edge();
        tmpEdgeA.vertex = vertex2;
        tmpEdgeA.dist = doMath;
        tmpEdge2.vertex = vertex1;
        tmpEdge2.dist = doMath;
        // add the new edges to the lists of edges of each vertex
        edges1.add(tmpEdgeA);
        edges2.add(tmpEdge2);
    }

    // finds index of attraction and adds it to road map
    public boolean addStop(String placeName) {
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            if (vertex.attractions.contains(placeName)) {
                places.add(i);
                return true;
            }
        }
        return false;
    }

    // adds place //
    public boolean addPlace(String cityName, String placeName) {
        if (containsVert(cityName) == false)
            return false;
        int vertexIndex = getIndex(cityName);
        vertices.get(vertexIndex).attractions.add(placeName);
        return true;
    }

    // finds path using algorthim
    public void dijkGrapgh(int start, ArrayList<Integer> dist, ArrayList<Integer> prev) {
        ArrayList<Integer> Unvisted = new ArrayList<Integer>();
        int unvisited;
        int visited;
        int visitedIndex;
        int unVisitedIndex;
        float currDist;
        float shortestDist;
        float tmpDist;

        // set ups and indexes points //
        for (visited = 0; visited < vertices.size(); visited++) {
            dist.add(-1);
            prev.add(-1);
            Unvisted.add(visited);
        }
        dist.set(start, 0);
        // cycles through all unvisited points for shortes path //
        while (Unvisted.isEmpty() == false) {
            unVisitedIndex = -1;
            shortestDist = -1;
            for (visitedIndex = 0; visitedIndex < Unvisted.size(); visitedIndex++) {
                visited = Unvisted.get(visitedIndex);
                currDist = dist.get(visited);
                if (currDist >= 0 && (unVisitedIndex < 0 || currDist < shortestDist)) { // if shorter, replace it
                    unVisitedIndex = visitedIndex;
                    shortestDist = currDist;
                }
            }
            unvisited = Unvisted.get(unVisitedIndex);
            Unvisted.remove(unVisitedIndex);
            for (visitedIndex = 0; visitedIndex < edges.get(unvisited).size(); visitedIndex++) {
                visited = edges.get(unvisited).get(visitedIndex).vertex;
                if (Unvisted.contains(visited)) {
                    tmpDist = dist.get(unvisited) + edges.get(unvisited).get(visitedIndex).dist;
                    if (dist.get(visited) < 0 || tmpDist < dist.get(visited)) { // if shorter, replace it
                        dist.set(visited, (int) tmpDist);
                        prev.set(visited, unvisited);
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------
    // returns shrotest path between two points
    private List<Integer> getPath(int first, int last, ArrayList<Integer> prev) {
        LinkedList<Integer> path = new LinkedList<Integer>();
        int curr = last;
        if (curr == first || prev.get(curr) >= 0)
            while (curr >= 0) {
                path.addFirst(curr);
                curr = prev.get(curr);
            }
        return path;
    }

    // branch of path calculation //
    private LinkedList<LinkedList<Integer>> allPaths(LinkedList<Integer> tmpArr) {
        LinkedList<LinkedList<Integer>> list = new LinkedList<LinkedList<Integer>>();
        int tmpArrSize = tmpArr.size();

        if (tmpArrSize == 1) { // is done when only one branch remains
            LinkedList<Integer> storeArr = new LinkedList<Integer>();
            storeArr.addLast(tmpArr.get(0));
            list.addLast(storeArr);
        } else {
            for (int i = 0; i < tmpArrSize; i++) {
                LinkedList<Integer> storeTmpArr = new LinkedList<Integer>();
                for (int j = 0; j < tmpArrSize; j++)
                    if (j != i)
                        storeTmpArr.add(tmpArr.get(j));
                LinkedList<LinkedList<Integer>> list1 = allPaths(storeTmpArr); // list clean up
                for (int j = 0; j < list1.size(); j++) {
                    LinkedList<Integer> arr = list1.get(j);
                    arr.add(0, tmpArr.get(i));
                    list.add(arr);
                }
            }
        }
        return list;
    }

    // *MAIN* Map route Calculator //
    public LinkedList<String> route(String starting_city, String ending_city) {
        int startingCityIndex = getIndex(starting_city);
        int endingCityIndex = getIndex(ending_city);

        boolean isPlace = !places.isEmpty();
        if (isPlace == false) // adds start if no places is given
            places.add(startingCityIndex);
        List<LinkedList<Integer>> listplaces = allPaths(places);
        int numPlaces = places.size();

        // add both ends to the end of the places list
        if (isPlace == true)
            places.add(startingCityIndex);
        places.add(endingCityIndex);

        int start = numPlaces;
        int end = numPlaces + 1;
        if (isPlace == false) {
            start = 0;
            end = 1;
        }

        // gets shortest path for start, places, and end //
        ArrayList<ArrayList<Integer>> dists = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> prevs = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < places.size(); i++) {
            int place = places.get(i);
            ArrayList<Integer> dist = new ArrayList<Integer>();
            ArrayList<Integer> prev = new ArrayList<Integer>();
            dijkGrapgh(place, dist, prev);
            dists.add(dist);
            prevs.add(prev);
        }

        // calculates shortest path //
        int dist = -1;
        int branchShortest = -1;
        for (int newPath = 0; newPath < listplaces.size(); newPath++) {
            LinkedList<Integer> branch = listplaces.get(newPath);
            int braDist = 0;
            // first branch
            int tmpEnd = branch.get(0);
            braDist += dists.get(start).get(tmpEnd);
            // following branches
            for (int i = 0; i < branch.size() - 1; i++) {
                int pointA = branch.get(i);
                int pointB = branch.get(i + 1);
                int pointAIndex = places.indexOf(pointA);
                braDist += dists.get(pointAIndex).get(pointB);
            }
            // final branch //
            tmpEnd = branch.get(branch.size() - 1);
            braDist += dists.get(end).get(tmpEnd);
            // stores if shorter path //
            if (newPath == 0 || dist > braDist) {
                dist = braDist;
                branchShortest = newPath;
            }
        }
        // -------------------------------------------------------------------------------------------------------
        // sets up final shortest route and returns it
        LinkedList<String> finalRoute = new LinkedList<String>(); // the final route to return
        LinkedList<Integer> finalList = listplaces.get(branchShortest);
        finalList.addFirst(startingCityIndex);
        finalList.addLast(endingCityIndex);
        for (int i = 0; i < finalList.size() - 1; i++) {
            int pointA = finalList.get(i);
            int PointB = finalList.get(i + 1);
            int pointAIndex = places.indexOf(pointA);
            List<Integer> path = getPath(pointA, PointB, prevs.get(pointAIndex));
            for (int j = 0; j < path.size() - 1; j++)
                finalRoute.addLast(vertices.get(path.get(j)).cityName);
        }
        finalRoute.addLast(vertices.get(endingCityIndex).cityName);
        return finalRoute;
    }
}
