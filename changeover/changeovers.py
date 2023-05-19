import random, time

def findBestOrdering(tasksString):
  tasks = parseTasks(tasksString)
  graph = createGraph(tasks)
  return graph.findBestOrdering()

def parseTasks(tasksString):
  return [Task(attributeString.split()) for attributeString in tasksString.split(',')]

class Task(object):
  def __init__(self, attributes):
    self.attributes = set(attributes)
  def isSubset(self, otherTask):
    return self.attributes.issubset(otherTask.attributes)
  def __repr__(self):
    return '"%s"' % ' '.join(map(str, sorted(self.attributes)))

def createGraph(tasks):
  nodes = [Node([task]) for task in tasks]
  random.shuffle(nodes)
  for subsetNode in nodes:
    for supersetNode in nodes:
      if subsetNode is not supersetNode and subsetNode.tasks[0].isSubset(supersetNode.tasks[0]):
        subsetNode.addDirectedEdge(supersetNode)
  return Graph(nodes)

class Node(object):
  def __init__(self, tasks):
    self.tasks = list(tasks)
    self.supersetEdges = set()
    self.subsetEdges = set()
  def __repr__(self):
    return 'Node{%s, %s, %s, %s}' % (id(self), self.tasks,
        [id(edge) for edge in self.supersetEdges],
        [id(edge) for edge in self.subsetEdges])
  def addDirectedEdge(self, supersetNode):
    self.supersetEdges.add(supersetNode)
    supersetNode.subsetEdges.add(self)
  def getDeepCopy(self):
    return Node(self.tasks)
  def getNextNodes(self):
    nodes = set()
    for node in self.supersetEdges:
      if node.subsetEdges.isdisjoint(self.supersetEdges):
        nodes.add(node)
    return nodes
  def getPreviousNodes(self):
    nodes = set()
    for node in self.subsetEdges:
      if node.supersetEdges.isdisjoint(self.subsetEdges):
        nodes.add(node)
    return nodes

class Graph(object):
  def __init__(self, nodes):
    self.nodes = set(nodes)
  def __repr__(self):
    return str(self.nodes)
  def getDeepCopy(self):
    nodeMap = {node: node.getDeepCopy() for node in self.nodes}
    for node in self.nodes:
      nodeMap[node].supersetEdges = set(nodeMap[edge] for edge in node.supersetEdges)
      nodeMap[node].subsetEdges = set(nodeMap[edge] for edge in node.subsetEdges)
    return Graph(nodeMap.values()), nodeMap
  def mergeNodes(self, subsetNode, supersetNode):
    node = Node(subsetNode.tasks + supersetNode.tasks)
    node.subsetEdges = subsetNode.subsetEdges
    node.supersetEdges = supersetNode.supersetEdges
    for edgeToRemove in subsetNode.supersetEdges:
      edgeToRemove.subsetEdges.remove(subsetNode)
    for edgeToRemove in supersetNode.subsetEdges:
      edgeToRemove.supersetEdges.remove(supersetNode)
    for subbysubsetNode in node.subsetEdges:
      subbysubsetNode.supersetEdges.remove(subsetNode)
      subbysubsetNode.supersetEdges.add(node)
    for superdupersetNode in node.supersetEdges:
      superdupersetNode.subsetEdges.remove(supersetNode)
      superdupersetNode.subsetEdges.add(node)
    self.nodes.remove(subsetNode)
    self.nodes.remove(supersetNode)
    self.nodes.add(node)
  def reduce(self):
    while True:
      nodes = list(self.nodes)
      random.shuffle(nodes)
      for node in nodes:
        nextNodes = node.getNextNodes()
        if len(nextNodes) == 1:
          self.mergeNodes(node, iter(nextNodes).next())
          break
        previousNodes = node.getPreviousNodes()
        if len(previousNodes) == 1:
          self.mergeNodes(iter(previousNodes).next(), node)
          break
      if len(nodes) == len(self.nodes): break
  def findBestOrdering(self):
    self.reduce()
    ordering = []
    nodes = list(self.nodes)
    random.shuffle(nodes)
    for node in nodes:
      if not node.supersetEdges and not node.subsetEdges:
        if ordering: ordering += ['(changeover)']
        ordering += node.tasks
        self.nodes.remove(node)
    if not self.nodes: return ordering
    node = random.choice(list(self.nodes))
    while node.subsetEdges:
      node = random.choice(list(node.subsetEdges))
    bestOrdering = None
    for supersetNode in node.supersetEdges:
      graph, nodeMap = self.getDeepCopy()
      graph.mergeNodes(nodeMap[node], nodeMap[supersetNode])
      thisOrdering = graph.findBestOrdering()
      if bestOrdering is None or len(thisOrdering) < len(bestOrdering):
        bestOrdering = thisOrdering
    if ordering: ordering += ['(changeover)']
    return ordering + bestOrdering

def test():
  tasks = set()
  while len(tasks) < 35:
    tasks.add(' '.join(sorted(random.sample('ABCDEFGHI', random.randint(3, 5)))))
  for i in 'ABCDEFGHI':
    tasks.add(i)
    for j in 'BCDEFGHI':
      if i < j: tasks.add('%s %s' % (i, j))
  tasks = list(tasks)
  random.shuffle(tasks)
  tasks = ', '.join(tasks)
  print tasks
  correctLen = None
  for i in xrange(3):
    timestamp = time.time()
    ordering = findBestOrdering(tasks)
    timestamp = time.time() - timestamp
    print
    print len(ordering), timestamp, ordering
    if correctLen is None: correctLen = len(ordering)
    elif correctLen != len(ordering): raise
