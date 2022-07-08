class Point {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }
}

class Edge {
    constructor(v1, v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    getOther(v) {
        if (v === this.v1) return this.v2;
        if (v === this.v2) return this.v1;

        return null;
    }

    get either() {
        return this.v1;
    }
}

class GridGraph {
    constructor(width, height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    get vertexCount() {
        return this.WIDTH * this.HEIGHT;
    }

    vertexForPoint(p) {
        if (p.x >= 0 && p.x < this.WIDTH && p.y >= 0 && p.y < this.HEIGHT) {
            return (p.y * this.WIDTH) + p.x;
        }

        return null;
    }

    pointForVertex(v) {
        if (v >= 0 && v < this.vertexCount) {
            const y = Math.floor(v / this.WIDTH);
            const x = v % this.WIDTH;

            return new Point(x, y);
        }

        return null;
    }

    adjacentEdges(v) {
        const adj = [];
        const p = this.pointForVertex(v);

        if (p.x > 0) adj.push(new Point(p.x - 1, p.y));
        if (p.x < this.WIDTH - 1) adj.push(new Point(p.x + 1, p.y));
        if (p.y > 0) adj.push(new Point(p.x, p.y - 1));
        if (p.y < this.WIDTH - 1) adj.push(new Point(p.x, p.y + 1));

        return adj.map( p => new Edge(v, this.vertexForPoint(p)));
    }
}

class PrimsMaze {
    constructor(conf) {
        this.G = new GridGraph(conf.numBlocksWide, conf.numBlocksHigh);
        this.collectedEdges = [];
        this.pendingEdges = [];
        this.markedVertices = [];

        for (let i = 0; i < this.G.vertexCount; i++) {
            this.markedVertices.push(false);
        }

        this.visit(0);
        while(this.pendingEdges.length > 0) {
            //let e = this.pendingEdges.pop();
            let e = this.choose(this.pendingEdges);
            let v = e.either;
            let w = e.getOther(v);

            if (this.markedVertices[v] && this.markedVertices[w]) continue;

            this.collectedEdges.push(e);

        console.log('collectedEdges: ');
        this.collectedEdges.forEach( e => {
            console.log(`  (${e.v1},${e.v2}) `);
        });


            if (!this.markedVertices[v]) this.visit(v);
            if (!this.markedVertices[w]) this.visit(w);
        }
    }

    choose(list) {
        const choiceIndex = Math.floor(Math.random() * list.length);
        const itemA = list.splice(choiceIndex, 1);

        return itemA[0];
    }

    visit(vertex) {
        console.log('visit: ', vertex);
        this.markedVertices[vertex] = true;
        console.log('markedVertices: ', this.markedVertices);
        this.G.adjacentEdges(vertex).forEach(e => {
            console.log('adj e: ', e);
            if (!this.markedVertices[e.getOther(vertex)]) this.pendingEdges.push(e);
        });

        console.log('pendingEdges: ');
        this.pendingEdges.forEach( e => {
            console.log(`  (${e.v1},${e.v2}) `);
        });
    }

    getGrid() {
        const mazeGrid = [];
        const pathVertices = new Set();

        for (let i = 0; i < this.G.HEIGHT; i++) {
            const row = [];
            for (let j = 0; j < this.G.WIDTH; j++) {
               row.push(false);
            }

            mazeGrid.push(row);
        }

        this.collectedEdges.forEach( e => {
            const v = e.either;
            pathVertices.add(v);
            pathVertices.add(e.getOther(v));
        });

        pathVertices.forEach( vertex => {
            const p = this.G.pointForVertex(vertex);
            mazeGrid[p.y][p.x] = true;
        });

        return mazeGrid;
    }
}


function generateGrid(conf) {
    const maze = new PrimsMaze(conf);

    return maze.getGrid();
}

function dumpGrid(conf, grid) {

    console.log('grid: ', grid);

    const mazeWidth = conf.blockSizePx * conf.numBlocksWide;
    const mazeHeight = conf.blockSizePx * conf.numBlocksHigh;
    const maze = document.querySelector('.maze-container');


    maze.innerHTML = "";

    maze.style.width = `${mazeWidth}px`;
    maze.style.height = `${mazeHeight}px`;

    const container = document.querySelector('.container');
    const containerWidth = mazeWidth * 1.2;
    container.style.width = `${containerWidth}px`;

    grid.forEach(row => {
        const mazeRow = document.createElement('div');
        mazeRow.classList.add('maze-row');

        row.forEach( cell => {
            const mazeCell = document.createElement('div');
            mazeCell.classList.add('maze-cell');
            mazeCell.classList.add( cell ? 'maze-path' : 'maze-block');
            mazeRow.appendChild(mazeCell);

            const blockSize = `${conf.blockSizePx}px`;
            mazeCell.style.width = blockSize;
            mazeCell.style.height = blockSize;
        });

        maze.appendChild(mazeRow);
    });
}

function drawMaze(conf) {
    const grid = generateGrid(conf);
    dumpGrid(conf, grid);
}

